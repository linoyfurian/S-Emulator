package semulator.logic.execution;

public interface ProgramExecutor {

    ExecutionRunDto run(int degreeOfExpansion, long runNumber, long... inputs);
}