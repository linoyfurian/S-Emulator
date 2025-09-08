package semulator.logic.instruction;

import semulator.logic.execution.ExecutionContext;
import semulator.logic.instruction.expansion.ExpansionUtils;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.variable.Variable;

import java.util.Map;
import java.util.Set;

public class NeutralInstruction extends AbstractInstruction implements UnexpandableInstruction,  SimpleInstruction {

    public NeutralInstruction(Variable variable, long instructionNumber, Instruction parent) {
        super(InstructionData.NEUTRAL, variable, InstructionType.BASIC, 0, instructionNumber, parent);
    }

    public NeutralInstruction(Variable variable, Label label, long instructionNumber, Instruction parent) {
        super(InstructionData.NEUTRAL, variable, label, InstructionType.BASIC, 0, instructionNumber, parent);
    }

    @Override
    public Label execute(ExecutionContext context) {
        return FixedLabel.EMPTY;
    }

    @Override
    public String getInstructionDescription() {
        String variableRepresentation=getVariable().getRepresentation();
        return (variableRepresentation + " <- " + variableRepresentation);
    }

    @Override
    public Instruction cloneInstructionWithNewNumber(long number){
        Instruction newInstruction = new NeutralInstruction(getVariable(), getLabel(), number, this.getParent());
        return newInstruction;
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

        newInstruction = new NeutralInstruction(newVariableImpl, newLabelImpl, instructionNumber, parent);

        return newInstruction;
    }

    @Override
    public Instruction cloneWithDifferentNumber(long number){
        Instruction newInstruction;
        newInstruction = new NeutralInstruction(this.getVariable(),this.getLabel(), number, this.getParent());
        return newInstruction;
    }
}