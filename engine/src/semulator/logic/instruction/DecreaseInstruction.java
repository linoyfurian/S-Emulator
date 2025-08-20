package semulator.logic.instruction;

import semulator.logic.execution.ExecutionContext;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.variable.Variable;

public class DecreaseInstruction extends AbstractInstruction implements UnexpandableInstruction {

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
        Instruction newInstruction = new DecreaseInstruction(getVariable(), getLabel(), number, null);
        return newInstruction;
    }
}