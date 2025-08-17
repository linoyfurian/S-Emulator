package semulator.logic.instruction.expansion;

import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.label.LabelImpl;
import semulator.logic.variable.Variable;
import semulator.logic.variable.VariableType;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ExpansionUtils {
    private ExpansionUtils() {} // private ctor – שלא יצרו מופעים

    public static int findAvailableLabelNumber(Set<Integer> usedLabelsNumbers) {
        int availableNumber = 1;

        while (usedLabelsNumbers.contains(availableNumber)) {
            availableNumber++;
        }
        return availableNumber;
    }

    public static int findAvailableZNumber(Set<Integer> usedZNumbers) {
        int availableNumber = 1;

        while (usedZNumbers.contains(availableNumber)) {
            availableNumber++;
        }
        return availableNumber;
    }

    public static Set<Integer> getSetOfUsedZNumbers(LinkedHashSet<Variable> variables) {
        Set<Integer> usedZNumbers = new HashSet<>();
        for (Variable variable : variables) {
            if(variable.getType()== VariableType.WORK){
                usedZNumbers.add(variable.getNumber());
            }
        }
        return usedZNumbers;
    }

    public static Set<Integer> getSetOfUsedLabels(LinkedHashSet<Label> labels) {
        Set<Integer> usedLabels = new HashSet<>();
        for (Label label : labels) {
            if(label instanceof LabelImpl labelImpl) {
                usedLabels.add(labelImpl.getNumber());
            }
        }
        return usedLabels;
    }
}

