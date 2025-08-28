package semulator.core;

import java.io.Serializable;
import java.util.List;
import semulator.logic.execution.ExecutionRunDto;
import semulator.logic.program.Program;

public class EngineState implements Serializable {
    private final Program program;
    private final boolean isLoaded;
    private final List<ExecutionRunDto> programRuns;

    EngineState(Program program, boolean isLoaded, List<ExecutionRunDto> programRuns) {
        this.program = program;
        this.isLoaded = isLoaded;
        this.programRuns = programRuns;
    }

    Program getProgram() {
        return program;
    }

    boolean getLoadedState() {
        return isLoaded;
    }

    List<ExecutionRunDto> getProgramRuns() {
        return programRuns;
    }
}
