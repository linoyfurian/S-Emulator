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

}
