package semulator.logic.instruction.expansion;

import semulator.core.loader.XmlProgramMapperV2;
import semulator.logic.Function.Function;
import semulator.logic.instruction.Instruction;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.label.LabelImpl;
import semulator.logic.program.Program;
import semulator.logic.variable.Variable;
import semulator.logic.variable.VariableImpl;
import semulator.logic.variable.VariableType;

import java.util.*;

public class ExpansionUtils {
    private ExpansionUtils() {}

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
            if(variable!=null){
                if(variable.getType() == VariableType.WORK){
                    usedZNumbers.add(variable.getNumber());
                }
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

    public static Variable generateNewVariable(Set<Integer> zUsedNumbers) {
        Variable newVariable = null;
        int newVariableNumber = 0;
        newVariableNumber = findAvailableZNumber(zUsedNumbers);
        zUsedNumbers.add(newVariableNumber);
        String newZVariableName = "z" + newVariableNumber;
        newVariable = new VariableImpl(VariableType.WORK, newVariableNumber, newZVariableName);

        return newVariable;
    }

    public static Variable validateOrCreateVariable(Variable variable, Set<Integer> zUsedNumbers, Map<String, String> oldAndNew) {
        Variable newVariableImpl;
        int variableNumber;
        String newVariable;

        variableNumber = variable.getNumber();

        if(oldAndNew.containsKey(variable.getRepresentation())){
            newVariable = oldAndNew.get(variable.getRepresentation());
            newVariableImpl = XmlProgramMapperV2.variableMapper(newVariable);
        }
        else{
            if(variable.getType() != VariableType.WORK){ //generate new work variable
                newVariableImpl = generateNewVariable(zUsedNumbers);
            }
            else{ //work variable
                if(zUsedNumbers.contains(variableNumber)){ //need different number
                    newVariableImpl = generateNewVariable(zUsedNumbers);
                }
                else{
                    newVariableImpl = variable;
                    zUsedNumbers.add(variableNumber);
                }
            }
            oldAndNew.put(variable.getRepresentation(), newVariableImpl.getRepresentation());
        }

        return newVariableImpl;
    }

    public static Label validateOrCreateLabel(Label label, Set<Integer> usedLabelsNumbers, Map<String, String> oldAndNew) {
        int labelNumber, newLabelNumber;
        String newLabel;
        Label newLabelImpl;

        //check label
        if(label instanceof LabelImpl gotoLabelImpl){
            labelNumber = gotoLabelImpl.getNumber();
            if(oldAndNew.containsKey(label.getLabelRepresentation())){
                newLabel = oldAndNew.get(gotoLabelImpl.getLabelRepresentation());
                newLabelImpl = new LabelImpl(Integer.parseInt(newLabel.substring(1)));
            }
            else{
                if(usedLabelsNumbers.contains(labelNumber)){
                    newLabelNumber = ExpansionUtils.findAvailableLabelNumber(usedLabelsNumbers);
                    usedLabelsNumbers.add(newLabelNumber);
                    newLabelImpl = new LabelImpl(newLabelNumber);
                }
                else{
                    newLabelImpl = label;
                    usedLabelsNumbers.add(labelNumber);
                }
                oldAndNew.put(label.getLabelRepresentation(), newLabelImpl.getLabelRepresentation());
            }
        }
        else if (label == FixedLabel.EXIT){
            if(oldAndNew.containsKey(label.getLabelRepresentation())){
                newLabel = oldAndNew.get(label.getLabelRepresentation());
                newLabelNumber =  Integer.parseInt(newLabel.substring(1));
                newLabelImpl = new LabelImpl(newLabelNumber);
            }
            else{
                newLabelNumber = ExpansionUtils.findAvailableLabelNumber(usedLabelsNumbers);
                usedLabelsNumbers.add(newLabelNumber);
                newLabelImpl = new LabelImpl(newLabelNumber);
                oldAndNew.put(label.getLabelRepresentation(), newLabelImpl.getLabelRepresentation());
            }
        }
        else
            newLabelImpl = FixedLabel.EMPTY;

        return newLabelImpl;
    }

    public static Function findFunctionInProgram(List<Program> functions, String functionName) {
        Function functionToRun = null;
        for (Program function : functions) {
            if(function instanceof Function f) {
                if(functionName.equals(f.getName())) {
                    functionToRun = f;
                    break;
                }
            }
        }
        return functionToRun;
    }
}

