package semulator.logic.instruction;

import semulator.logic.execution.ExecutionContext;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.variable.Variable;

public class IncreaseInstruction extends AbstractInstruction implements UnexpandableInstruction {

    public IncreaseInstruction(Variable variable, long instructionNumber, Instruction parent) {
        super(InstructionData.INCREASE, variable, InstructionType.BASIC,0, instructionNumber, parent);
    }

    public IncreaseInstruction(Variable variable, Label label, long instructionNumber, Instruction parent) {
        super(InstructionData.INCREASE, variable, label, InstructionType.BASIC,0, instructionNumber, parent);
    }

    @Override
    public Label execute(ExecutionContext context) {

        long variableValue = context.getVariableValue(getVariable());
        variableValue++;
        context.updateVariable(getVariable(), variableValue);

        return FixedLabel.EMPTY;
    }

    @Override
    public String getInstructionDescription() {
        String variableRepresentation = getVariable().getRepresentation();
        return (variableRepresentation + " <- " + variableRepresentation + " + 1");
    }

    @Override
    public Instruction cloneInstructionWithNewNumber(long number){
        Instruction newInstruction = new IncreaseInstruction(getVariable(), getLabel(), number, this.getParent());
        return newInstruction;
    }
}