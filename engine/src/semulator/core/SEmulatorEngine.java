package semulator.core;

import jakarta.xml.bind.JAXBException;
import semulator.core.loader.LoadReport;
import semulator.logic.execution.ExecutionContext;
import semulator.logic.program.ProgramDto;

import java.nio.file.Path;

public interface SEmulatorEngine {

    LoadReport loadProgramDetails(Path filePath) throws JAXBException;
    ProgramDto displayProgram();
    ProgramDto expand(int desiredDegreeOfExpand);
    ExecutionContext runProgram(int desiredDegreeOfExpand, Long ... input);
    //history
    //exit
}
