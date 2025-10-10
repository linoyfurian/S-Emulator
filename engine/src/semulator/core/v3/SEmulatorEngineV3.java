package semulator.core.v3;

import dto.*;
import jakarta.xml.bind.JAXBException;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface SEmulatorEngineV3 {
    ProgramFunctionDto displayProgram(String name, boolean isProgram);
    LoadReport loadProgramDetails(InputStream in, String username) throws JAXBException;
    List<ProgramInfo> getPrograms();
    List<FunctionInfo> getFunctions();
    ProgramFunctionDto expand(String programName, boolean isProgramBool, int degreeOfExpand);
    ExecutionRunDto runProgram(String username, int desiredDegreeOfExpand, String programName, boolean isProgramBool, Map<String, Long> originalInputs, long ... input);
    DebugContextDto initialStartOfDebugger(String username, String programName, boolean isProgram, int degreeOfRun, DebugContextDto debugContext, Map<String, Long> originalInputs, long ... inputs);
    DebugContextDto debug (String username, String programName, boolean isProgram, int degreeOfExpand, DebugContextDto debugContext, Map<String, Long> originalInputs);
    void addCurrentRunToHistory(DebugContextDto debugContext, int degreeOfRun, String programName, boolean isProgram, String architecture);
    List<RunResultDto> getUserRunHistory(String user);
}
