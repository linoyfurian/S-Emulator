package semulator.logic.execution;

import semulator.logic.program.Program;
import semulator.logic.variable.Variable;

import java.util.Map;

public interface ProgramExecutor {

    ExecutionRunDto run(int degreeOfExpansion, long runNumber, long... inputs);
}
