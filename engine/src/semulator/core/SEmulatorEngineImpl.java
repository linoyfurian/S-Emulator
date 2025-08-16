package semulator.core;

import semulator.logic.program.ProgramImpl;

import java.nio.file.Path;

public class SEmulatorEngineImpl implements SEmulatorEngine {
    private ProgramImpl program;
    private boolean isLoaded;

    public void setProgram(ProgramImpl program) {
        this.program = program;
        this.isLoaded = true;
    }

    @Override
    public ProgramImpl displayProgram(){
        return program;
    }

    @Override
    public void loadProgramDetails(Path filePath){
    }
}
