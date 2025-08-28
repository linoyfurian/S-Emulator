package semulator.core;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import semulator.api.LoadReport;
import semulator.core.loader.XmlProgramMapper;
import semulator.core.loader.jaxb.schema.generated.SProgram;
import semulator.api.dto.ExecutionRunDto;
import semulator.logic.execution.ProgramExecutor;
import semulator.logic.execution.ProgramExecutorImpl;
import semulator.logic.program.Program;
import semulator.api.dto.ProgramDto;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.nio.file.Path;

public class SEmulatorEngineImpl implements SEmulatorEngine {
    private Program program = null;
    private boolean isLoaded = false;
    private List<ExecutionRunDto> programRuns = new ArrayList<>();

    @Override
    public ProgramDto displayProgram(){
        return new ProgramDto(program);
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
            JAXBContext ctx = JAXBContext.newInstance("semulator.core.loader.jaxb.schema.generated");
            Unmarshaller u = ctx.createUnmarshaller();
            SProgram sProgram = (SProgram) u.unmarshal(filePath.toFile());
            Program mappedProgram;
            mappedProgram = XmlProgramMapper.fromSProgramToProgramImpl(sProgram);
            boolean isValidProgram = mappedProgram.validate();
            if (isValidProgram) {
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
    public ExecutionRunDto runProgram(int desiredDegreeOfExpand, long ... input){
        Program programToRun = program.expand(desiredDegreeOfExpand);
        ProgramExecutor programExecutor = new ProgramExecutorImpl(programToRun);
        long runNumber = this.programRuns.size()+1;

        ExecutionRunDto runResult = programExecutor.run(desiredDegreeOfExpand, runNumber, input);
        this.programRuns.add(runResult);

        return runResult;
    }

    @Override
    public ProgramDto expand(int desiredDegreeOfExpand){
        Program expandedProgram = program.expand(desiredDegreeOfExpand);
        ProgramDto programDto = new ProgramDto(expandedProgram);
        return programDto;
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
    public void saveState(Path filePath) throws IOException {
        if (filePath == null) {
            throw new IOException("Error: Path is null");
        }
        EngineState currentState = new EngineState(this.program, this.isLoaded, this.programRuns);

        try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(Files.newOutputStream(filePath)))) {
            oos.writeObject(currentState);
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

            this.program = state.getProgram();
            this.isLoaded = state.getLoadedState();
            this.programRuns = new ArrayList<>(state.getProgramRuns());
        }
    }
}