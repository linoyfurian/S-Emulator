package semulator.logic.instruction;

import semulator.logic.execution.ExecutionContext;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.variable.Variable;

public class JumpEqualVariableInstruction extends AbstractInstruction implements JumpInstruction{

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
        return ("IF " + getVariable().getRepresentation() + "=" + variableName.getRepresentation() + " GOTO " + JEVariableLabel.getLabelRepresentation());
    }

    @Override
    public Label getTargetLabel() {
        return JEVariableLabel;
    }

    public Variable getVariableName() {
        return variableName;
    }
}