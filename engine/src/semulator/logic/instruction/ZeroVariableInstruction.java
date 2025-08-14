package semulator.logic.instruction;

import semulator.logic.execution.ExecutionContext;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.variable.Variable;

import java.util.List;

public class ZeroVariableInstruction extends AbstractInstruction {
    public ZeroVariableInstruction(List<Variable> variables) {
        super(InstructionData.ZERO_VARIABLE, variables);
    }

    public ZeroVariableInstruction(List<Variable> variables, Label label) {
        super(InstructionData.ZERO_VARIABLE, variables, label);
    }

    @Override
    public Label execute(ExecutionContext context) {

        long variableValue = context.getVariableValue(getVariables().get(0));
        variableValue=0;
        context.updateVariable(getVariables().get(0), variableValue);

        return FixedLabel.EMPTY;
    }
}

