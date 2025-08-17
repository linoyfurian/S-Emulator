package semulator.logic.execution;

import semulator.logic.program.Program;
import semulator.logic.variable.Variable;

import java.util.Map;

public interface ProgramExecutor {

    ExecutionContext run(Long... inputs);
    Map<Variable, Long> variableState();
    //Program expand(int degree, Program program);
}
