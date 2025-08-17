package semulator.logic.execution;

import semulator.logic.program.Program;
import semulator.logic.variable.Variable;

import java.util.Map;

public interface ProgramExecutor {

    ExecutionContext run(Long... input);
    Map<Variable, Long> variableState();
    Program expand(int degree);
    void setProgramToRun(Program programToRun);
}
