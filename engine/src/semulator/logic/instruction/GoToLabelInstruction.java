package semulator.logic.instruction;

import semulator.logic.execution.ExecutionContext;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.variable.Variable;

import java.util.List;

public class GoToLabelInstruction extends AbstractInstruction {

    private final Label gotoLabel;

    public GoToLabelInstruction(List<Variable> variables, Label gotoLabel) {
        super(InstructionData.GOTO_LABEL, variables, InstructionType.SYNTHETIC, 1);
        this.gotoLabel = gotoLabel;
    }

    public GoToLabelInstruction(List<Variable> variables, Label label, Label gotoLabel) {
        super(InstructionData.GOTO_LABEL, variables, label, InstructionType.SYNTHETIC, 1);
        this.gotoLabel = gotoLabel;
    }

    @Override
    public Label execute(ExecutionContext context) {
        return gotoLabel;
    }
}
