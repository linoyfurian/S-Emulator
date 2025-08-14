package semulator.logic.instruction;

import semulator.logic.execution.ExecutionContext;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.variable.Variable;
import java.util.List;

public class DecreaseInstruction extends AbstractInstruction {

    public DecreaseInstruction(List<Variable> variables) {
        super(InstructionData.DECREASE, variables, InstructionType.BASIC, 0);
    }

    public DecreaseInstruction(List<Variable> variables, Label label) {
        super(InstructionData.DECREASE, variables, label, InstructionType.BASIC, 0);
    }

    @Override
    public Label execute(ExecutionContext context) {

        long variableValue = context.getVariableValue(getVariables().get(0));
        variableValue = Math.max(0, variableValue - 1);
        context.updateVariable(getVariables().get(0), variableValue);

        return FixedLabel.EMPTY;
    }
}
