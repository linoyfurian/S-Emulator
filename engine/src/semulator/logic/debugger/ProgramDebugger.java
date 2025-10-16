package semulator.logic.debugger;

import dto.DebugContextDto;

import java.util.Map;

public interface ProgramDebugger {
    DebugContextDto debug (long credits, long instructionToExecuteNumber, DebugContextDto debugDetails, Map<String, Long> originalInputs);
    DebugContextDto resume (long credits, long instructionToExecuteNumber, DebugContextDto debugDetails, Map<String, Long> originalInputs);
    DebugContextDto initialDebugger(Map<String, Long> originalInputs);
    DebugContextDto breakPointMode(long breakPointIndex, DebugContextDto result, Map<String, Long> originalInputs);
}
