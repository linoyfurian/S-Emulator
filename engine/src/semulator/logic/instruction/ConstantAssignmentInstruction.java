package semulator.logic.instruction;

import semulator.logic.execution.ExecutionContext;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.variable.Variable;

import java.util.List;

public class ConstantAssignmentInstruction extends AbstractInstruction {

    private final long constantValue;

    public ConstantAssignmentInstruction(List<Variable> variables, long constantValue) {
        super(InstructionData.CONSTANT_ASSIGNMENT, variables);
        this.constantValue = constantValue;
    }

    public ConstantAssignmentInstruction(List<Variable> variables, Label label, long constantValue) {
        super(InstructionData.CONSTANT_ASSIGNMENT, variables, label);
        this.constantValue = constantValue;
    }

    @Override
    public Label execute(ExecutionContext context) {

        long variableValue = context.getVariableValue(getVariables().get(0));
        variableValue = constantValue;
        context.updateVariable(getVariables().get(0), variableValue);

        return FixedLabel.EMPTY;
    }
}
