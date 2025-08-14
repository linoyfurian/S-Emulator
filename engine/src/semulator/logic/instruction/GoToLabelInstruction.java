package semulator.logic.instruction;

import semulator.logic.execution.ExecutionContext;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.variable.Variable;

public class GoToLabelInstruction extends AbstractInstruction {

    private final Label gotoLabel;

    public GoToLabelInstruction(Variable variable, Label gotoLabel) {
        super(InstructionData.GOTO_LABEL, variable);
        this.gotoLabel = gotoLabel;
    }

    public GoToLabelInstruction(Variable variable, Label label, Label gotoLabel) {
        super(InstructionData.GOTO_LABEL, variable, label);
        this.gotoLabel = gotoLabel;
    }

    @Override
    public Label execute(ExecutionContext context) {
        return gotoLabel;
    }
}
