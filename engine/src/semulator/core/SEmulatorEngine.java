package semulator.core;

import jakarta.xml.bind.JAXBException;
import semulator.core.loader.LoadReport;
import semulator.logic.execution.ExecutionRunDto;
import semulator.logic.program.ProgramDto;

import java.nio.file.Path;
import java.util.List;

public interface SEmulatorEngine {

    LoadReport loadProgramDetails(Path filePath) throws JAXBException;
    ProgramDto displayProgram();
    ProgramDto expand(int desiredDegreeOfExpand);
    ExecutionRunDto runProgram(int desiredDegreeOfExpand, long ... input);
    List<ExecutionRunDto> historyDisplay();
}