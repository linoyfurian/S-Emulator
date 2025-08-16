package semulator.logic.instruction;

import semulator.logic.execution.ExecutionContext;
import semulator.logic.label.Label;
import semulator.logic.variable.Variable;

public class GoToLabelInstruction extends AbstractInstruction {

    private final Label gotoLabel;

    public GoToLabelInstruction(Variable variable, Label gotoLabel, long instructionNumber) {
        super(InstructionData.GOTO_LABEL, variable, InstructionType.SYNTHETIC, 1, instructionNumber);
        this.gotoLabel = gotoLabel;
    }

    public GoToLabelInstruction(Variable variable, Label label, Label gotoLabel, long instructionNumber) {
        super(InstructionData.GOTO_LABEL, variable, label, InstructionType.SYNTHETIC, 1, instructionNumber);
        this.gotoLabel = gotoLabel;
    }

    @Override
    public Label execute(ExecutionContext context) {
        return gotoLabel;
    }

    @Override
    public String toString() {
        return String.format(
                "#%d (%s) [%s] %s (%d)",
                getInstructionNumber(),
                getType().getType(),
                getLabel().getLabelRepresentation(),
                getInstructionDescription(),
                cycles());
    }

    @Override
    public String getInstructionDescription() {
        return ("GOTO " + gotoLabel.getLabelRepresentation());
    }
}