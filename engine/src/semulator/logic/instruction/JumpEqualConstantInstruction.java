package semulator.logic.instruction;

import semulator.logic.execution.ExecutionContext;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.variable.Variable;

public class JumpEqualConstantInstruction extends AbstractInstruction {

    private final long constantValue;
    private final Label JEConstantLabel;

    public JumpEqualConstantInstruction(Variable variable, long constantValue, Label JEConstantLabel, long instructionNumber) {
        super(InstructionData.JUMP_EQUAL_CONSTANT, variable, InstructionType.SYNTHETIC, 3, instructionNumber);
        this.constantValue = constantValue;
        this.JEConstantLabel = JEConstantLabel;
    }

    public JumpEqualConstantInstruction(Variable variable, Label label, long constantValue, Label JEConstantLabel, long instructionNumber) {
        super(InstructionData.JUMP_EQUAL_CONSTANT, variable, label, InstructionType.SYNTHETIC, 3, instructionNumber);
        this.constantValue = constantValue;
        this.JEConstantLabel = JEConstantLabel;
    }


    @Override
    public Label execute(ExecutionContext context) {

        long variableValue = context.getVariableValue(getVariable());
        if(variableValue == constantValue)
            return JEConstantLabel;

        return FixedLabel.EMPTY;
    }
}
