package semulator.logic.execution;

import semulator.api.dto.ExecutionRunDto;

import java.util.Map;

public interface ProgramExecutor {
    ExecutionRunDto run(int degreeOfExpansion, long runNumber, Map<String, Long> originalInputs, long... inputs);
}