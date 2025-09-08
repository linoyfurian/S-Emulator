package semulator.logic.variable;

import java.io.Serializable;
import java.util.Objects;

public class VariableImpl implements Variable, Serializable {

    private final VariableType type;
    private final int number;
    private final String variableName;

    public VariableImpl(VariableType type, int number, String VariableName) {
        this.type = type;
        this.number = number;
        this.variableName = VariableName;
    }

    @Override
    public VariableType getType() {
        return type;
    }

    @Override
    public int getNumber() {
        return number;
    }

    @Override
    public String getRepresentation() {
        if(type==VariableType.FUNCTION)
            return variableName;
        else
            return type.getVariableRepresentation(number);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        Variable variable = (Variable) o;
        return Objects.equals(this.getRepresentation(), variable.getRepresentation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getRepresentation());
    }
}
