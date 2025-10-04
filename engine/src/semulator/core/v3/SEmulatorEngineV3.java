package semulator.core.v3;

import dto.LoadReport;
import jakarta.xml.bind.JAXBException;
import semulator.api.dto.ProgramFunctionDto;

import java.io.InputStream;

public interface SEmulatorEngineV3 {
    ProgramFunctionDto displayProgram(String name, boolean isProgram);
    LoadReport loadProgramDetails(InputStream in) throws JAXBException;
}
