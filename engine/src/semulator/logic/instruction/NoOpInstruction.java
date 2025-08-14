package semulator.logic.instruction;

import semulator.logic.execution.ExecutionContext;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.variable.Variable;

public class NoOpInstruction extends AbstractInstruction {

    public NoOpInstruction(Variable variable, long instructionNumber) {
        super(InstructionData.NO_OP, variable, InstructionType.BASIC, 0, instructionNumber);
    }

    public NoOpInstruction(Variable variable, Label label, long instructionNumber) {
        super(InstructionData.NO_OP, variable, label, InstructionType.BASIC, 0, instructionNumber);
    }

    @Override
    public Label execute(ExecutionContext context) {
        return FixedLabel.EMPTY;
    }
}