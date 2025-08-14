package semulator.logic.instruction;

import semulator.logic.execution.ExecutionContext;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.variable.Variable;

public class JumpZeroInstruction extends AbstractInstruction{

    private final Label jzLabel;

    public JumpZeroInstruction(Variable variable, Label jzLabel, long instructionNumber) {
        super(InstructionData.JUMP_ZERO, variable, InstructionType.SYNTHETIC, 2, instructionNumber);
        this.jzLabel = jzLabel;
    }

    public JumpZeroInstruction(Variable variable, Label jzLabel, Label label, long instructionNumber) {
        super(InstructionData.JUMP_ZERO, variable, label, InstructionType.SYNTHETIC, 2, instructionNumber);
        this.jzLabel = jzLabel;
    }

    @Override
    public Label execute(ExecutionContext context) {
        long variableValue = context.getVariableValue(getVariable());

        if (variableValue == 0) {
            return jzLabel;
        }
        return FixedLabel.EMPTY;
    }

    @Override
    public String toString() {
        String description = "IF " + getVariable().getRepresentation() + "=0" + " GOTO " + jzLabel.getLabelRepresentation();
        return String.format(
                "#%d (%s) [%s] %s (%d)",
                getInstructionNumber(),
                getType().getType(),
                getLabel().getLabelRepresentation(),
                description,
                cycles());
    }
}