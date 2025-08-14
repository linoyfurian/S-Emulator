package semulator.logic.instruction;

import semulator.logic.execution.ExecutionContext;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.variable.Variable;
import java.util.List;

public class JumpEqualVariableInstruction extends AbstractInstruction {

    private final Label JEVariableLabel;

    public JumpEqualVariableInstruction(List<Variable> variables, Label JEVariableLabel) {
        super(InstructionData.JUMP_EQUAL_VARIABLE, variables);
        this.JEVariableLabel = JEVariableLabel;
    }

    public JumpEqualVariableInstruction(List<Variable> variables, Label label, Label JEVariableLabel) {
        super(InstructionData.JUMP_EQUAL_VARIABLE, variables, label);
        this.JEVariableLabel = JEVariableLabel;
    }

    @Override
    public Label execute(ExecutionContext context) {
        long variable1Value = context.getVariableValue(getVariables().get(0));
        long variable2Value = context.getVariableValue(getVariables().get(1));
        if (variable1Value == variable2Value)
            return JEVariableLabel;

        return FixedLabel.EMPTY;
    }
}