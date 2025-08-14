package semulator.logic.instruction;

import semulator.logic.execution.ExecutionContext;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.variable.Variable;


public class AssignmentInstruction extends AbstractInstruction{

    private final Variable assignedVariable;

    public AssignmentInstruction(Variable variable, long instructionNumber, Variable assignedVariable) {
        super(InstructionData.ASSIGNMENT, variable, InstructionType.SYNTHETIC, 2, instructionNumber);
        this.assignedVariable = assignedVariable;
    }

    public AssignmentInstruction(Variable variable, Label label, long instructionNumber, Variable assignedVariable) {
        super(InstructionData.ASSIGNMENT, variable,  label, InstructionType.SYNTHETIC, 2, instructionNumber);
        this.assignedVariable = assignedVariable;
    }

    @Override
    public Label execute(ExecutionContext context) {

        long assignedValue = context.getVariableValue(assignedVariable);
        context.updateVariable(getVariable(), assignedValue);

        return FixedLabel.EMPTY;
    }

    @Override
    public String toString() {
        String description = getVariable().getRepresentation() + " <- " + assignedVariable.getRepresentation();
        return String.format(
                "#%d (%s) [%s] %s (%d)",
                getInstructionNumber(),
                getType().getType(),
                getLabel().getLabelRepresentation(),
                description,
                cycles());
    }
}