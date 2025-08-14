package semulator.logic.instruction;

import semulator.logic.execution.ExecutionContext;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.variable.Variable;

public class JumpNotZeroInstruction extends AbstractInstruction{

    private final Label jnzLabel;

    public JumpNotZeroInstruction(Variable variable, Label jnzLabel, long instructionNumber) {
        super(InstructionData.JUMP_NOT_ZERO, variable, InstructionType.BASIC, 0, instructionNumber);
        this.jnzLabel = jnzLabel;
    }

    public JumpNotZeroInstruction(Variable variable, Label jnzLabel, Label label, long instructionNumber) {
        super(InstructionData.JUMP_NOT_ZERO, variable, label, InstructionType.BASIC, 0, instructionNumber);
        this.jnzLabel = jnzLabel;
    }

    @Override
    public Label execute(ExecutionContext context) {
        long variableValue = context.getVariableValue(getVariable());

        if (variableValue != 0) {
            return jnzLabel;
        }
        return FixedLabel.EMPTY;
    }
}
