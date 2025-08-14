package semulator.logic.label;

public class LabelImpl implements Label{

    private final String label;

    public LabelImpl(int number) {
        if(number>=10)
            label = "L" + number;
        else
            label = "L" + number + " ";
    }

    public String getLabelRepresentation() {
        return label;
    }
}