package semulator.core;

import jakarta.xml.bind.JAXBException;
import semulator.api.LoadReport;
import semulator.api.dto.*;
import semulator.logic.program.Program;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public interface SEmulatorEngine {

    LoadReport loadProgramDetails(Path filePath) throws JAXBException;
    ProgramFunctionDto displayProgram();
    ProgramFunctionDto expand(int desiredDegreeOfExpand);
    ExecutionRunDto runProgram(int desiredDegreeOfExpand, Map<String, Long> originalInputs, long... input);
    void setLoaded(boolean isLoaded);
    boolean isLoaded();
    int getMaxDegreeOfExpand();
    String getProgramName();
    //void resetProgramRuns();
    void saveState(Path filePath) throws IOException;
    void loadState(Path filePath) throws IOException, ClassNotFoundException;
    int getProgramInContextMaxDegreeOfExpand();
    String getProgramInContextName();
    void setProgramInContext(String programInContextName);
    List<String> getProgramOrFunctionNames();
    DebugContextDto debugProgram(int desiredDegreeOfExpand, DebugContextDto context, Map<String, Long> originalInputs, long ... input);
    void addCurrentRunToHistory(DebugContextDto debugContext, int degreeOfRun);
    List<RunResultDto> getProgramInContextRunHistory();
    DebugContextDto resumeProgram(int desiredDegreeOfExpand, DebugContextDto context, Map<String, Long> originalInputs, long ... input);
    void uploadCreatedProgram(ProgramDraft newProgram);
    DebugContextDto initialStartOfDebugger(int degreeOfRun, DebugContextDto debugContext, Map<String, Long> originalInputs, long ... inputs);
    void saveCreatedProgramToFile(ProgramDraft newProgram, File fileToSave);
    }