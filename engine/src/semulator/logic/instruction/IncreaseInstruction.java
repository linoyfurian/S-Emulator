package semulator.logic.instruction;

import semulator.logic.execution.ExecutionContext;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.variable.Variable;

import java.util.List;

public class IncreaseInstruction extends AbstractInstruction {

    public IncreaseInstruction(List<Variable> variables) {
        super(InstructionData.INCREASE, variables, InstructionType.BASIC,0);
    }

    public IncreaseInstruction(List<Variable> variables, Label label) {
        super(InstructionData.INCREASE, variables, label, InstructionType.BASIC,0);
    }

    @Override
    public Label execute(ExecutionContext context) {

        long variableValue = context.getVariableValue(getVariables().get(0));
        variableValue++;
        context.updateVariable(getVariables().get(0), variableValue);

        return FixedLabel.EMPTY;
    }
}
