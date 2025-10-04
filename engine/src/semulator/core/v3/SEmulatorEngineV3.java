package semulator.core.v3;

import dto.FunctionInfo;
import dto.LoadReport;
import dto.ProgramInfo;
import jakarta.xml.bind.JAXBException;
import semulator.api.dto.ProgramFunctionDto;

import java.io.InputStream;
import java.util.List;

public interface SEmulatorEngineV3 {
    ProgramFunctionDto displayProgram(String name, boolean isProgram);
    LoadReport loadProgramDetails(InputStream in, String username) throws JAXBException;
    List<ProgramInfo> getPrograms();
    List<FunctionInfo> getFunctions();

}
