package semulator.logic.instruction;

import semulator.logic.execution.ExecutionContext;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.variable.Variable;

import java.util.List;

public class JumpZeroInstruction extends AbstractInstruction{

    private final Label jzLabel;

    public JumpZeroInstruction(List<Variable> variables, Label jzLabel) {
        super(InstructionData.JUMP_ZERO, variables, InstructionType.SYNTHETIC, 2);
        this.jzLabel = jzLabel;
    }

    public JumpZeroInstruction(List<Variable> variables, Label jzLabel, Label label) {
        super(InstructionData.JUMP_ZERO, variables, label, InstructionType.SYNTHETIC, 2);
        this.jzLabel = jzLabel;
    }

    @Override
    public Label execute(ExecutionContext context) {
        long variableValue = context.getVariableValue(getVariables().get(0));

        if (variableValue == 0) {
            return jzLabel;
        }
        return FixedLabel.EMPTY;
    }
}