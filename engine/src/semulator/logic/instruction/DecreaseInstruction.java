package semulator.logic.instruction;

import semulator.logic.execution.ExecutionContext;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.variable.Variable;

public class DecreaseInstruction extends AbstractInstruction {

    public DecreaseInstruction(Variable variable, long instructionNumber) {
        super(InstructionData.DECREASE, variable, InstructionType.BASIC, 0, instructionNumber);
    }

    public DecreaseInstruction(Variable variable, Label label, long instructionNumber) {
        super(InstructionData.DECREASE, variable, label, InstructionType.BASIC, 0, instructionNumber);
    }

    @Override
    public Label execute(ExecutionContext context) {

        long variableValue = context.getVariableValue(getVariable());
        variableValue = Math.max(0, variableValue - 1);
        context.updateVariable(getVariable(), variableValue);

        return FixedLabel.EMPTY;
    }
}
