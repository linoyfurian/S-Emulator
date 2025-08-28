package semulator.logic.execution;

import semulator.api.dto.ExecutionRunDto;

public interface ProgramExecutor {

    ExecutionRunDto run(int degreeOfExpansion, long runNumber, long... inputs);
}