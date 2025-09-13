package semulator.core;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import semulator.api.LoadReport;
import semulator.api.dto.*;
import semulator.core.loader.XmlProgramMapperV2;
import semulator.core.loader.jaxb.schema.version2.generated.SProgram;
import semulator.logic.Function.Function;
import semulator.logic.debugger.ProgramDebugger;
import semulator.logic.debugger.ProgramDebuggerImpl;
import semulator.logic.execution.ProgramExecutor;
import semulator.logic.execution.ProgramExecutorImpl;
import semulator.logic.instruction.expansion.ExpansionUtils;
import semulator.logic.program.Program;
import semulator.logic.program.ProgramImpl;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.nio.file.Path;

public class SEmulatorEngineImpl implements SEmulatorEngine {
    private Program program = null;
    private Program programInContext = null;
    private boolean isLoaded = false;
    private List<ExecutionRunDto> programRuns = new ArrayList<>();

    @Override
    public ProgramFunctionDto displayProgram(){
        if(programInContext == null){
            return null;
        }

        if(programInContext instanceof ProgramImpl)
            return new ProgramDto(programInContext);
        else
            return new FunctionDto(programInContext, program);
    }

    @Override
    public LoadReport loadProgramDetails(Path filePath) throws JAXBException {
        LoadReport loadReport = null;
        if (filePath == null) {
            loadReport = new LoadReport(false, "Error: No path was provided");
            return loadReport;
        }
        if (!hasXmlExtension(filePath)) { // not xml
            loadReport = new LoadReport(false, "Error: No XML  file. File must have .xml extension");
            return loadReport;
        }

        if (!Files.exists(filePath)) {
            loadReport = new LoadReport(false, "Error: File " + filePath.getFileName() + " does not exist");
            return  loadReport;
        }

        try {
            JAXBContext ctx = JAXBContext.newInstance("semulator.core.loader.jaxb.schema.version2.generated");
            Unmarshaller u = ctx.createUnmarshaller();
            SProgram sProgram = (SProgram) u.unmarshal(filePath.toFile());
            Program mappedProgram;
            mappedProgram = XmlProgramMapperV2.fromSProgramToProgramImpl(sProgram);
            boolean isValidProgram = mappedProgram.validate();
            if (isValidProgram) {
                this.programInContext = mappedProgram;
                this.program = mappedProgram;
                this.isLoaded = true;
                loadReport = new LoadReport(true, "Program loaded successfully");
                return loadReport;
            }
            else {
                loadReport = new LoadReport(false, "Error: Program is not valid, there is a reference to a label that doesnt exits");
                return loadReport;
            }
        }
        catch (JAXBException e) {
            loadReport = new LoadReport(false, e.getMessage());
            return loadReport;
        }
    }

    private static boolean hasXmlExtension(Path path){
        if (path == null) return false;
        String name = path.getFileName().toString().trim();
        return name.toLowerCase(Locale.ROOT).endsWith(".xml");
    }

    @Override
    public DebugContextDto debugProgram(int desiredDegreeOfExpand, DebugContextDto context, long ... input){
        Program programToRun = programInContext.expand(desiredDegreeOfExpand);
        ProgramDebugger debugger;
        if(context == null)
            debugger = new ProgramDebuggerImpl(programToRun, this.program, input);
        else
            debugger = new ProgramDebuggerImpl(programToRun, context, this.program);

        long instructionToExecuteNumber = 1;
        if(context!=null)
            instructionToExecuteNumber = context.getNextInstructionNumber();

        DebugContextDto result;
        result = debugger.debug(instructionToExecuteNumber, context);

        return result;
    }



    @Override
    public ExecutionRunDto runProgram(int desiredDegreeOfExpand, long ... input){
        Program programToRun = programInContext.expand(desiredDegreeOfExpand);
        ProgramExecutor programExecutor = new ProgramExecutorImpl(programToRun);
       //todo long runNumber = this.programRuns.size()+1;


        //todo map history
        ExecutionRunDto runResult = programExecutor.run(desiredDegreeOfExpand, 1, input);
        this.programRuns.add(runResult);

        return runResult;
    }

    @Override
    public ProgramFunctionDto expand(int desiredDegreeOfExpand){
        ProgramFunctionDto programFunctionDto;
        Program expandedProgram;

        if(programInContext == null){
            return  null;
        }

        expandedProgram = programInContext.expand(desiredDegreeOfExpand);
        if(expandedProgram instanceof ProgramImpl)
            programFunctionDto = new ProgramDto(expandedProgram);
        else
            programFunctionDto = new FunctionDto(expandedProgram, program);

        return programFunctionDto;
    }

    @Override
    public List<ExecutionRunDto> historyDisplay(){
        return this.programRuns;
    }

    @Override
    public void setLoaded(boolean isLoaded){
        this.isLoaded = isLoaded;
    }

    @Override
    public boolean isLoaded() {
        return isLoaded;
    }

    @Override
    public int getMaxDegreeOfExpand(){
        return this.program.calculateMaxDegree();
    }

    @Override
    public String getProgramName(){
        return this.program.getName();
    }

    @Override
    public void resetProgramRuns(){
        this.programRuns = new ArrayList<>();
    }

    @Override
    public int getProgramInContextMaxDegreeOfExpand(){
        return this.programInContext.calculateMaxDegree();
    }

    @Override
    public String getProgramInContextName(){
        return this.programInContext.getName();
    }

    @Override
    public void setProgramInContext(String programInContextName){
        if(programInContextName == null)
            return;
        if(programInContextName.equals(program.getName()))
            this.programInContext = this.program;
        else{
            ProgramImpl programImpl = (ProgramImpl) program;
            List<Program> functions = programImpl.getFunctions();

            Program programInContext = ExpansionUtils.findFunctionInProgramAccordingToUserString(functions, programInContextName);
            this.programInContext = programInContext;
        }
    }

    //todo fix engine state
    @Override
    public void saveState(Path filePath) throws IOException {
        if (filePath == null) {
            throw new IOException("Error: Path is null");
        }
       // EngineState currentState = new EngineState(this.program, this.isLoaded, this.programRuns);

        try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(Files.newOutputStream(filePath)))) {
           // oos.writeObject(currentState);
            oos.flush();
        }
    }

    @Override
    public void loadState(Path filePath) throws IOException, ClassNotFoundException {
        if (filePath == null) {
            throw new IOException("Error: Path is null");
        }
        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("Error: File not found: " + filePath);
        }

        try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(Files.newInputStream(filePath)))) {
            Object obj = ois.readObject();
            EngineState state = (EngineState) obj;

          //  this.program = state.getProgram();
            this.isLoaded = state.getLoadedState();
            this.programRuns = new ArrayList<>(state.getProgramRuns());
        }
    }

    @Override
    public List<String> getProgramOrFunctionNames(){
        List<String> programOrFunctionNames = new ArrayList<>();
        programOrFunctionNames.add(program.getName());
        ProgramImpl programImpl = (ProgramImpl) program;
        List<Program> functions = programImpl.getFunctions();

        for (Program function : functions) {
            Function asFunction = (Function) function;
            programOrFunctionNames.add(asFunction.getUserString());
        }

        return programOrFunctionNames;
    }
}