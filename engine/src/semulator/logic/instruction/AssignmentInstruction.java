package semulator.logic.instruction;

import semulator.logic.execution.ExecutionContext;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.variable.Variable;

import java.util.List;

public class AssignmentInstruction extends AbstractInstruction{

    public AssignmentInstruction(List<Variable> variables) {
        super(InstructionData.ASSIGNMENT, variables);
    }

    public AssignmentInstruction(List<Variable> variables, Label label) {
        super(InstructionData.ASSIGNMENT, variables,  label);
    }

    @Override
    public Label execute(ExecutionContext context) {

        long variable1Value = context.getVariableValue(getVariables().get(0));
        long variable2Value = context.getVariableValue(getVariables().get(1));
        variable1Value = variable2Value;
        context.updateVariable(getVariables().get(0), variable1Value);

        return FixedLabel.EMPTY;
    }
}