package semulator.logic.instruction;

import semulator.logic.execution.ExecutionContext;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.variable.Variable;

public class ConstantAssignmentInstruction extends AbstractInstruction {

    private final long constantValue;

    public ConstantAssignmentInstruction(Variable variable, long constantValue, long instructionNumber) {
        super(InstructionData.CONSTANT_ASSIGNMENT, variable, InstructionType.SYNTHETIC, 2, instructionNumber);
        this.constantValue = constantValue;
    }

    public ConstantAssignmentInstruction(Variable variable, Label label, long constantValue, long instructionNumber) {
        super(InstructionData.CONSTANT_ASSIGNMENT, variable, label, InstructionType.SYNTHETIC, 2, instructionNumber);
        this.constantValue = constantValue;
    }

    @Override
    public Label execute(ExecutionContext context) {

        context.updateVariable(getVariable(), constantValue);

        return FixedLabel.EMPTY;
    }
}
