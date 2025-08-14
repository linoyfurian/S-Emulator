package semulator.logic.instruction;

import semulator.logic.execution.ExecutionContext;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.variable.Variable;

public class IncreaseInstruction extends AbstractInstruction {

    public IncreaseInstruction(Variable variable, long instructionNumber) {
        super(InstructionData.INCREASE, variable, InstructionType.BASIC,0, instructionNumber);
    }

    public IncreaseInstruction(Variable variable, Label label, long instructionNumber) {
        super(InstructionData.INCREASE, variable, label, InstructionType.BASIC,0, instructionNumber);
    }

    @Override
    public Label execute(ExecutionContext context) {

        long variableValue = context.getVariableValue(getVariable());
        variableValue++;
        context.updateVariable(getVariable(), variableValue);

        return FixedLabel.EMPTY;
    }

    @Override
    public String toString() {
        String variableRepresentation = getVariable().getRepresentation();
        String description = variableRepresentation + " <- " + variableRepresentation + " + 1";
        return String.format(
                "#%d (%s) [%s] %s (%d)",
                getInstructionNumber(),
                getType().getType(),
                getLabel().getLabelRepresentation(),
                description,
                cycles());
    }
}
