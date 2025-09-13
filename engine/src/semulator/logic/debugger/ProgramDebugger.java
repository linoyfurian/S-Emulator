package semulator.logic.debugger;

import semulator.api.dto.DebugContextDto;

public interface ProgramDebugger {
    DebugContextDto debug (long instructionToExecuteNumber, DebugContextDto debugDetails);
}
