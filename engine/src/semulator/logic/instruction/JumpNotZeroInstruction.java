package semulator.logic.instruction;

import semulator.logic.execution.ExecutionContext;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.variable.Variable;

import java.util.List;

public class JumpNotZeroInstruction extends AbstractInstruction{

    private final Label jnzLabel;

    public JumpNotZeroInstruction(List<Variable> variables, Label jnzLabel) {
        super(InstructionData.JUMP_NOT_ZERO, variables);
        this.jnzLabel = jnzLabel;
    }

    public JumpNotZeroInstruction(List<Variable> variables, Label jnzLabel, Label label) {
        super(InstructionData.JUMP_NOT_ZERO, variables, label);
        this.jnzLabel = jnzLabel;
    }

    @Override
    public Label execute(ExecutionContext context) {
        long variableValue = context.getVariableValue(getVariables().get(0));

        if (variableValue != 0) {
            return jnzLabel;
        }
        return FixedLabel.EMPTY;
    }
}
