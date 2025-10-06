package semulator.logic.execution;

import dto.ExecutionRunDto;

import java.util.Map;

public interface ProgramExecutor {
    ExecutionRunDto run(int degreeOfExpansion, long runNumber, Map<String, Long> originalInputs, long... inputs);
}