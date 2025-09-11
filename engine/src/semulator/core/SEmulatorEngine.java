package semulator.core;

import jakarta.xml.bind.JAXBException;
import semulator.api.LoadReport;
import semulator.api.dto.ExecutionRunDto;
import semulator.api.dto.ProgramDto;
import semulator.api.dto.ProgramFunctionDto;
import semulator.logic.program.Program;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface SEmulatorEngine {

    LoadReport loadProgramDetails(Path filePath) throws JAXBException;
    ProgramFunctionDto displayProgram();
    ProgramFunctionDto expand(int desiredDegreeOfExpand);
    ExecutionRunDto runProgram(int desiredDegreeOfExpand, long... input);
    List<ExecutionRunDto> historyDisplay();
    void setLoaded(boolean isLoaded);
    boolean isLoaded();
    int getMaxDegreeOfExpand();
    String getProgramName();
    void resetProgramRuns();
    void saveState(Path filePath) throws IOException;
    void loadState(Path filePath) throws IOException, ClassNotFoundException;
    int getProgramInContextMaxDegreeOfExpand();
    String getProgramInContextName();
    void setProgramInContext(String programInContextName);
    List<String> getProgramOrFunctionNames();
}