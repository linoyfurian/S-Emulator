package semulator.logic.instruction;

import semulator.core.loader.XmlProgramMapperV2;
import semulator.logic.execution.ExecutionContext;
import semulator.logic.instruction.expansion.ExpansionUtils;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.label.LabelImpl;
import semulator.logic.variable.Variable;
import semulator.logic.variable.VariableType;

import java.util.Map;
import java.util.Set;

public class DecreaseInstruction extends AbstractInstruction implements UnexpandableInstruction,  SimpleInstruction {

    public DecreaseInstruction(Variable variable, long instructionNumber, Instruction parent) {
        super(InstructionData.DECREASE, variable, InstructionType.BASIC, 0, instructionNumber, parent);
    }

    public DecreaseInstruction(Variable variable, Label label, long instructionNumber, Instruction parent) {
        super(InstructionData.DECREASE, variable, label, InstructionType.BASIC, 0, instructionNumber, parent);
    }

    @Override
    public Label execute(ExecutionContext context) {

        long variableValue = context.getVariableValue(getVariable());
        variableValue = Math.max(0, variableValue - 1);
        context.updateVariable(getVariable(), variableValue);

        return FixedLabel.EMPTY;
    }

    @Override
    public String getInstructionDescription() {
        String variableRepresentation = getVariable().getRepresentation();
        return (variableRepresentation + " <- " + variableRepresentation + " - 1");
    }

    @Override
    public Instruction cloneInstructionWithNewNumber(long number){
        Instruction newInstruction = new DecreaseInstruction(getVariable(), getLabel(), number, this.getParent());
        return newInstruction;
    }

    @Override
    public Instruction QuoteFunctionExpandHelper(Set<Integer> zUsedNumbers, Set<Integer> usedLabelsNumbers, long instructionNumber, Map<String, String> oldAndNew, Instruction parent){
        Instruction newInstruction;
        Variable variable, newVariableImpl;
        Label label, newLabelImpl;
        //check label
        label = this.getLabel();
        newLabelImpl = ExpansionUtils.validateOrCreateLabel(label, usedLabelsNumbers, oldAndNew);

        //check variable
        variable = this.getVariable();
        newVariableImpl = ExpansionUtils.validateOrCreateVariable(variable, zUsedNumbers, oldAndNew);

        newInstruction = new DecreaseInstruction(newVariableImpl, newLabelImpl, instructionNumber, parent);

        return newInstruction;
    }

    @Override
    public Instruction cloneWithDifferentNumber(long number){
        Instruction newInstruction;
        newInstruction = new DecreaseInstruction(this.getVariable(),this.getLabel(), number, this.getParent());
        return newInstruction;
    }
}