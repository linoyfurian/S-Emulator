package semulator.logic.instruction;

import semulator.logic.execution.ExecutionContext;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.variable.Variable;

import java.util.List;

public class NoOpInstruction extends AbstractInstruction {

    public NoOpInstruction(List<Variable> variables) {
        super(InstructionData.NO_OP, variables, InstructionType.BASIC, 0);
    }

    public NoOpInstruction(List<Variable> variables, Label label) {
        super(InstructionData.NO_OP, variables, label, InstructionType.BASIC, 0);
    }

    @Override
    public Label execute(ExecutionContext context) {
        return FixedLabel.EMPTY;
    }
}