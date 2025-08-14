package semulator.logic.instruction;

import semulator.logic.execution.ExecutionContext;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.variable.Variable;

public class JumpEqualVariableInstruction extends AbstractInstruction {

    private final Label JEVariableLabel;
    private final Variable variableName;

    public JumpEqualVariableInstruction(Variable variable, Label JEVariableLabel, Variable variableName, long instructionNumber) {
        super(InstructionData.JUMP_EQUAL_VARIABLE, variable, InstructionType.SYNTHETIC, 3, instructionNumber);
        this.JEVariableLabel = JEVariableLabel;
        this.variableName = variableName;
    }

    public JumpEqualVariableInstruction(Variable variable, Label label, Label JEVariableLabel, Variable variableName, long instructionNumber) {
        super(InstructionData.JUMP_EQUAL_VARIABLE, variable, label, InstructionType.SYNTHETIC, 3, instructionNumber);
        this.JEVariableLabel = JEVariableLabel;
        this.variableName = variableName;
    }

    @Override
    public Label execute(ExecutionContext context) {
        long firstVariableValue = context.getVariableValue(getVariable());
        long secondVariableValue = context.getVariableValue(variableName);

        if (firstVariableValue == secondVariableValue)
            return JEVariableLabel;

        return FixedLabel.EMPTY;
    }
}