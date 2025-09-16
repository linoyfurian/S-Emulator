package semulator.logic.execution;

import semulator.logic.variable.Variable;

import java.util.HashMap;
import java.util.Map;

public class ExecutionContextImpl implements ExecutionContext {
    private final Map<String, Long> variableValues = new HashMap<>();

    public ExecutionContextImpl() {}

    public ExecutionContextImpl(Map<String, Long> variableValues) {
        for(String variableName : variableValues.keySet()) {
            this.variableValues.put(variableName, variableValues.get(variableName));
        }
    }

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
        Map<String, Long> result = new HashMap<>();
        result.putAll(this.variableValues);
        return result;
    }

}
