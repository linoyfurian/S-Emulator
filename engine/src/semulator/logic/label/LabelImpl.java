package semulator.logic.label;

import java.io.Serializable;
import java.util.Objects;

public class LabelImpl implements Label, Serializable {

    private final String label;
    private final int number;

    public LabelImpl(int number) {
            label = "L" + number;
            this.number = number;
    }

    public String getLabelRepresentation() {
        return label;
    }

    public int getNumber() {
        return number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        Label label = (Label) o;
        return Objects.equals(this.getLabelRepresentation(), label.getLabelRepresentation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getLabelRepresentation());
    }
}