package semulator.logic.instruction;

import semulator.logic.execution.ExecutionContext;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.variable.Variable;
import java.util.List;

public class JumpEqualConstantInstruction extends AbstractInstruction {

    private final long constantValue;
    private final Label JEConstantLabel;

    public JumpEqualConstantInstruction(List<Variable> variables, long constantValue, Label JEConstantLabel) {
        super(InstructionData.JUMP_EQUAL_CONSTANT, variables, InstructionType.SYNTHETIC, 3);
        this.constantValue = constantValue;
        this.JEConstantLabel = JEConstantLabel;
    }

    public JumpEqualConstantInstruction(List<Variable> variables, Label label, long constantValue, Label JEConstantLabel) {
        super(InstructionData.JUMP_EQUAL_CONSTANT, variables, label, InstructionType.SYNTHETIC, 3);
        this.constantValue = constantValue;
        this.JEConstantLabel = JEConstantLabel;
    }


    @Override
    public Label execute(ExecutionContext context) {

        long variableValue = context.getVariableValue(getVariables().get(0));
        if(variableValue == constantValue)
            return JEConstantLabel;

        return FixedLabel.EMPTY;
    }
}
