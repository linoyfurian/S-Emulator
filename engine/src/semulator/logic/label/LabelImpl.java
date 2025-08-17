package semulator.logic.label;

public class LabelImpl implements Label{

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
}