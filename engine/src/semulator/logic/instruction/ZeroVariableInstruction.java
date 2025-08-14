package semulator.logic.instruction;

import semulator.logic.execution.ExecutionContext;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.variable.Variable;

public class ZeroVariableInstruction extends AbstractInstruction {
    public ZeroVariableInstruction(Variable variable, long instructionNumber) {
        super(InstructionData.ZERO_VARIABLE, variable, InstructionType.SYNTHETIC, 1, instructionNumber);
    }

    public ZeroVariableInstruction(Variable variable, Label label, long instructionNumber) {
        super(InstructionData.ZERO_VARIABLE, variable, label, InstructionType.SYNTHETIC, 1, instructionNumber);
    }

    @Override
    public Label execute(ExecutionContext context) {

        context.updateVariable(getVariable(), 0);

        return FixedLabel.EMPTY;
    }

    @Override
    public String toString() {
        String description = getVariable().getRepresentation() + " <- 0";
        return String.format(
                "#%d (%s) [%s] %s (%d)",
                getInstructionNumber(),
                getType().getType(),
                getLabel().getLabelRepresentation(),
                description,
                cycles());
    }
}

