package semulator.core;

import jakarta.xml.bind.JAXBException;
import semulator.core.loader.LoadReport;
import semulator.logic.execution.ExecutionRunDto;
import semulator.logic.program.ProgramDto;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface SEmulatorEngine {

    LoadReport loadProgramDetails(Path filePath) throws JAXBException;
    ProgramDto displayProgram();
    ProgramDto expand(int desiredDegreeOfExpand);
    ExecutionRunDto runProgram(int desiredDegreeOfExpand, long... input);
    List<ExecutionRunDto> historyDisplay();
    void setLoaded(boolean isLoaded);
    boolean isLoaded();
    int getMaxDegreeOfExpand();
    String getProgramName();
    void resetProgramRuns();
    void saveState(Path filePath) throws IOException;
    void loadState(Path filePath) throws IOException, ClassNotFoundException;
}