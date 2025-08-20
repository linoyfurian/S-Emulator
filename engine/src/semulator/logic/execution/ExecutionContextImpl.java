package semulator.logic.execution;

import semulator.logic.program.Program;
import semulator.logic.variable.Variable;

import java.util.HashMap;
import java.util.Map;

public class ExecutionContextImpl implements ExecutionContext {
    private final Map<String, Long> variableValues = new HashMap<>();

    @Override
    public long getVariableValue(Variable v) {
        return variableValues.getOrDefault(v.getRepresentation(), 0L);
    }

    @Override
    public void updateVariable(Variable v, long value) {
        variableValues.put(v.getRepresentation(), value);
    }

    @Override
    public Map<String, Long> getAllValues() {
        return variableValues;
    }

}
