package semulator.logic.instruction;

import semulator.logic.execution.ExecutionContext;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.variable.Variable;

public class AssignmentInstruction extends AbstractInstruction{

    public AbstractInstruction(Variable variable) {
        super(InstructionData.DECREASE, variable);
    }

    public AbstractInstruction(Variable variable, Label label) {
        super(InstructionData.DECREASE, variable, label);
    }

    @Override
    public Label execute(ExecutionContext context) {

        long variableValue = context.getVariableValue(getVariable());
        variableValue = Math.max(0, variableValue - 1);
        context.updateVariable(getVariable(), variableValue);

        return FixedLabel.EMPTY;
    }
}
