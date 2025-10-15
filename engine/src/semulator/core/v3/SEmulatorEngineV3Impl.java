package semulator.core.v3;

import dto.*;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import semulator.core.loader.XmlProgramMapperV2;
import semulator.core.loader.jaxb.schema.version2.generated.SProgram;
import semulator.logic.Function.Function;
import semulator.logic.debugger.ProgramDebugger;
import semulator.logic.debugger.ProgramDebuggerImpl;
import semulator.logic.execution.ProgramExecutor;
import semulator.logic.execution.ProgramExecutorImpl;
import semulator.logic.program.Program;
import semulator.logic.program.ProgramImpl;
import semulator.logic.program.ProgramStatistics;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SEmulatorEngineV3Impl implements  SEmulatorEngineV3 {
    private Map<String, Program> programs = new HashMap<>();
    private Map<String, Program> functions = new HashMap<>();
    private Map<String, List<RunResultDto>> runsHistory = new HashMap<>();
    private Map<String, ProgramStatistics>  programStatistics = new HashMap<>();

    private static final JAXBContext JAXB_CTX;
    static {
        try {
            JAXB_CTX = JAXBContext.newInstance("semulator.core.loader.jaxb.schema.version2.generated");
        } catch (JAXBException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @Override
    public ProgramFunctionDto displayProgram(String name, boolean isProgram) {
        if (isProgram) {
            Program program = programs.get(name);
            return new ProgramDto(program, functions);
        } else {
            for(Program function : functions.values()) {
                Function f = (Function) function;
                if(f.getUserString().equals(name)) {
                    return new FunctionDto(function, functions);
                }
            }
        }
        return null;
    }

    @Override
    public LoadReport loadProgramDetails(InputStream in, String username) throws JAXBException {
        if (in == null) {
            return new LoadReport(false, "Error: No input stream was provided");
        }

        try {
            Unmarshaller u = JAXB_CTX.createUnmarshaller();
            SProgram sProgram = (SProgram) u.unmarshal(in);

            if(this.programs.containsKey(sProgram.getName())){
                return new LoadReport(false, "Error: Program with this name is already exist");
            }
            Program mappedProgram = XmlProgramMapperV2.fromSProgramToProgramImpl(sProgram, username);


            boolean isValidProgram = mappedProgram.validate();
            if (!isValidProgram) {
                return new LoadReport(false, "Error: Program is not valid, there is a reference to a label that doesnt exits");
            }

            ProgramImpl programImpl = (ProgramImpl) mappedProgram;

            if (programImpl.hasInvalidFunctionReferences(functions)) {
                return new LoadReport(false, "Error: Program is not valid, there is a reference in the program to a function that doesnt exits");
            }

            List<Program> funcs = programImpl.getFunctions();
            for (Program f : funcs) {
                if(this.functions.containsKey(f.getName())){
                    return new LoadReport(false, "Error: Program is not valid, there is a reference to a function that already exits");
                }
                Function fn = (Function) f;
                if (fn.hasInvalidFunctionReferences(funcs, functions)) {
                    return new LoadReport(false, "Error: Program is not valid, there is a reference in a function to a function that doesnt exits");
                }
            }

            this.programs.put(mappedProgram.getName(), mappedProgram);

            for (Program f : funcs) {
                Function fn = (Function) f;
                this.functions.put(fn.getName(), fn);
            }

            programStatistics.put(mappedProgram.getName(),new ProgramStatistics(0,0));

            return new LoadReport(true, "Program loaded successfully",1, funcs.size());

        } catch (JAXBException e) {
            return new LoadReport(false, e.getMessage());
        }
    }

    @Override
    public List<ProgramInfo> getPrograms(){
        List<ProgramInfo> result = new ArrayList<>();
        ProgramInfo newProgram;
        ProgramStatistics programStatistic;
        for(Program program : programs.values()){
            programStatistic = programStatistics.get(program.getName());
            newProgram = new ProgramInfo(program.getName(),program.getUsername(),program.getInstructions().size(),program.calculateMaxDegree(),programStatistic.getCreditsAverage(),programStatistic.getRunsNumber());
            result.add(newProgram);
        }
        return result;
    }

    @Override
    public List<FunctionInfo> getFunctions(){
        List<FunctionInfo> result = new ArrayList<>();
        FunctionInfo newFunction;
        for(Program function : functions.values()){
            Function func = (Function) function;
            newFunction = new FunctionInfo(func.getUserString(),func.getProgramParent(),func.getUsername(), func.getInstructions().size(),func.calculateMaxDegree());
            result.add(newFunction);
        }
        return result;
    }

    @Override
    public ProgramFunctionDto expand(String programName, boolean isProgramBool, int degreeOfExpand){
        ProgramFunctionDto programFunctionDto;
        Program expandedProgram;

        if(programName == null){
            return  null;
        }

        Program programInContext;

        if(isProgramBool)
            programInContext = programs.get(programName);
        else
            programInContext = functions.get(programName);

        expandedProgram = programInContext.expand(degreeOfExpand, functions);

        if(expandedProgram instanceof ProgramImpl)
            programFunctionDto = new ProgramDto(expandedProgram, functions);
        else
            programFunctionDto = new FunctionDto(expandedProgram, functions);

        return programFunctionDto;
    }

    @Override
    public ExecutionRunDto runProgram(int credits, String username, String architecture, int desiredDegreeOfExpand, String programName, boolean isProgramBool, Map<String, Long> originalInputs, long ... input){
        Program programToRun;

        if(programName == null){
            return  null;
        }
        Program programInContext;
        if(isProgramBool)
            programInContext = programs.get(programName);
        else
            programInContext = functions.get(programName);

        programToRun = programInContext.expand(desiredDegreeOfExpand, functions);

        ProgramExecutor programExecutor = new ProgramExecutorImpl(programToRun, functions);
        ExecutionRunDto runResult = programExecutor.run(credits, desiredDegreeOfExpand, 1, originalInputs, input);

        String programOrFunction = "Program";
        if(!isProgramBool){
            programOrFunction = "Function";
        }

        if(!this.runsHistory.containsKey(username)){
            this.runsHistory.put(username, new ArrayList<>());
        }

        List<RunResultDto> results = this.runsHistory.get(username);
        RunResultDto currentRunResult = new RunResultDto(results.size()+1, desiredDegreeOfExpand, runResult.getResult(), runResult.getCycles(), originalInputs, runResult.getVariables(), programOrFunction, architecture, programName);
        results.add(currentRunResult);

        if(isProgramBool){
            ProgramStatistics programStatistic =  programStatistics.get(programName);
            programStatistic.updateCreditsAverage(currentRunResult.getCycles());
        }
        return runResult;
    }

    @Override
    public DebugContextDto initialStartOfDebugger(String username, String programName, boolean isProgram, int degreeOfRun, DebugContextDto debugContext, Map<String, Long> originalInputs, long... inputs) {
        DebugContextDto result;
        Program programToRun;

        if(programName == null){
            return  null;
        }
        Program programInContext;
        if(isProgram)
            programInContext = programs.get(programName);
        else
            programInContext = functions.get(programName);

        programToRun = programInContext.expand(degreeOfRun, functions);


        ProgramDebugger debugger;
        debugger = new ProgramDebuggerImpl(username, programToRun, functions, originalInputs, inputs);
        result = debugger.initialDebugger(originalInputs);

        return  result;
    }

    @Override
    public DebugContextDto debug (int credits, String username, String programName, boolean isProgram, int degreeOfExpand, DebugContextDto debugContext, Map<String, Long> originalInputs){
        DebugContextDto result;
        Program programToRun;

        if(programName == null){
            return  null;
        }
        Program programInContext;
        if(isProgram)
            programInContext = programs.get(programName);
        else
            programInContext = functions.get(programName);

        programToRun = programInContext.expand(degreeOfExpand, functions);

        ProgramDebugger debugger;
        debugger = new ProgramDebuggerImpl(username, programToRun, debugContext, functions);

        long instructionToExecuteNumber = 1;
        if(debugContext!=null)
            instructionToExecuteNumber = debugContext.getNextInstructionNumber();

        result = debugger.debug(credits, instructionToExecuteNumber, debugContext, originalInputs);

        return result;
    }

    @Override
    public void addCurrentRunToHistory(DebugContextDto debugContext, int degreeOfRun, String programName, boolean isProgram, String architecture){
        String userName = debugContext.getUserName();
        if(!this.runsHistory.containsKey(userName)){
            this.runsHistory.put(userName, new ArrayList<>());
        }

        List<RunResultDto> results = this.runsHistory.get(userName);
        Map<String, Long> variablesValues = debugContext.getCurrentVariablesValues();
        long resultY = variablesValues.get("y");
        String programOrFunction = "Program";
        if(!isProgram)
            programOrFunction = "Function";
        RunResultDto currentRunResult = new RunResultDto(results.size()+1, degreeOfRun, resultY, debugContext.getCycles(), debugContext.getOriginalInputs(), debugContext.getCurrentVariablesValues(), programOrFunction, architecture, programName);
        results.add(currentRunResult);

        if(isProgram){
            ProgramStatistics programStatistic =  programStatistics.get(programName);
            programStatistic.updateCreditsAverage(currentRunResult.getCycles());
        }
    }

    @Override
    public List<RunResultDto> getUserRunHistory(String user){
        if(this.runsHistory.get(user)==null)
            return new ArrayList<>();
        return this.runsHistory.get(user);
    }

    @Override
    public DebugContextDto resume (int credits, String username, String programName, boolean isProgram, int degreeOfExpand, DebugContextDto debugContext, Map<String, Long> originalInputs){
        Program programToRun;

        if(programName == null){
            return  null;
        }
        Program programInContext;
        if(isProgram)
            programInContext = programs.get(programName);
        else
            programInContext = functions.get(programName);

        programToRun = programInContext.expand(degreeOfExpand, functions);

        ProgramDebugger debugger;
        debugger = new ProgramDebuggerImpl(username, programToRun, debugContext, functions);


        long instructionToExecuteNumber = 1;

        if(debugContext!=null)
            instructionToExecuteNumber = debugContext.getNextInstructionNumber();

        DebugContextDto result;
        result = debugger.resume(credits, instructionToExecuteNumber, debugContext, originalInputs);
        return result;
    }
}
