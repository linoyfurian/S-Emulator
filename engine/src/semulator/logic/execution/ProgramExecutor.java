package semulator.logic.execution;

import dto.ExecutionRunDto;

import java.util.Map;

public interface ProgramExecutor {
    ExecutionRunDto run(long credits, int degreeOfExpansion, long runNumber, Map<String, Long> originalInputs, long... inputs);
}