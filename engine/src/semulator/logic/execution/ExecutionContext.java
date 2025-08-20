package semulator.logic.execution;

import semulator.logic.variable.Variable;

import java.util.Map;

public interface ExecutionContext {

    long getVariableValue(Variable v);
    void updateVariable(Variable v, long value);
    Map<String, Long> getAllValues();
}
