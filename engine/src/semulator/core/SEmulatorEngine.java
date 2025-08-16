package semulator.core;


import semulator.logic.program.ProgramImpl;

import java.nio.file.Path;
import java.util.Map;

public interface SEmulatorEngine {

    void loadProgramDetails(Path filePath);
    ProgramImpl displayProgram();
    //expand
    //run
    //history
    //exit
}
