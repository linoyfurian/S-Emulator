package semulator.core;

import jakarta.xml.bind.JAXBException;
import semulator.logic.execution.ExecutionContext;
import semulator.logic.program.ProgramData;

import java.nio.file.Path;

public interface SEmulatorEngine {

    void loadProgramDetails(Path filePath) throws JAXBException;
    ProgramData displayProgram();
    //expand
    ExecutionContext runProgram(int desiredDegreeOfExpand, Long ... input);
    //history
    //exit
}
