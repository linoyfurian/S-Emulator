package semulator.logic.instruction;

import semulator.logic.execution.ExecutionContext;
import semulator.logic.instruction.expansion.ExpansionUtils;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.label.LabelImpl;
import semulator.logic.variable.Variable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ZeroVariableInstruction extends AbstractInstruction implements ExpandableInstruction, SimpleInstruction {
    public ZeroVariableInstruction(Variable variable, long instructionNumber, Instruction parent) {
        super(InstructionData.ZERO_VARIABLE, variable, InstructionType.SYNTHETIC, 1, instructionNumber, parent);
    }

    public ZeroVariableInstruction(Variable variable, Label label, long instructionNumber, Instruction parent) {
        super(InstructionData.ZERO_VARIABLE, variable, label, InstructionType.SYNTHETIC, 1, instructionNumber, parent);
    }

    @Override
    public Label execute(ExecutionContext context) {

        context.updateVariable(getVariable(), 0);

        return FixedLabel.EMPTY;
    }

    @Override
    public String getInstructionDescription() {
        return (getVariable().getRepresentation() + " <- 0");
    }

    @Override
    public List<Instruction> expand(Set<Integer> zUsedNumbers, Set<Integer> usedLabelsNumbers, long instructionNumber){
        List<Instruction> nextInstructions = new ArrayList<>();
        int newLabelNumber;
        Label newLabel, currentLabel;

        currentLabel = this.getLabel();
        if(currentLabel != FixedLabel.EMPTY) {
            newLabel=currentLabel;
        }
        else{
            newLabelNumber = ExpansionUtils.findAvailableLabelNumber(usedLabelsNumbers);
            usedLabelsNumbers.add(newLabelNumber);
            newLabel = new LabelImpl(newLabelNumber);
        }

        Instruction newInstruction = new DecreaseInstruction(this.getVariable(), newLabel, instructionNumber, this);
        nextInstructions.add(newInstruction);

        instructionNumber++;

        Instruction newInstruction2 = new JumpNotZeroInstruction(this.getVariable(), newLabel, instructionNumber, this);
        nextInstructions.add(newInstruction2);

        return nextInstructions;
    }

    @Override
    public Instruction QuoteFunctionExpandHelper(Set<Integer> zUsedNumbers, Set<Integer> usedLabelsNumbers, long instructionNumber, Map<String, String> oldAndNew, Instruction parent){
        Instruction newInstruction;
        Label label, newLabelImpl;
        Variable variable, newVariableImpl;

        //check label
        label = this.getLabel();
        newLabelImpl = ExpansionUtils.validateOrCreateLabel(label, usedLabelsNumbers, oldAndNew);

        //check variable
        variable = this.getVariable();
        newVariableImpl = ExpansionUtils.validateOrCreateVariable(variable, zUsedNumbers, oldAndNew);

        newInstruction = new ZeroVariableInstruction(newVariableImpl, newLabelImpl, instructionNumber, parent);

        return newInstruction;
    }

    @Override
    public Instruction cloneWithDifferentNumber(long number){
        Instruction newInstruction;
        newInstruction = new ZeroVariableInstruction(this.getVariable(), this.getLabel(), number, this.getParent());
        return newInstruction;
    }
}