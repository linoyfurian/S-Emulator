package semulator.logic.execution;

import semulator.logic.variable.Variable;

public interface ExecutionContext {

    long getVariableValue(Variable v);
    void updateVariable(Variable v, long value);
}
