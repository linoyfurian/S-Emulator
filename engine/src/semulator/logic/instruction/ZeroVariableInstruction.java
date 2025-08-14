package semulator.logic.instruction;

import semulator.logic.execution.ExecutionContext;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.variable.Variable;

import java.util.List;

public class ZeroVariableInstruction extends AbstractInstruction {
    public ZeroVariableInstruction(List<Variable> variables) {
        super(InstructionData.ZERO_VARIABLE, variables, InstructionType.SYNTHETIC, 1);
    }

    public ZeroVariableInstruction(List<Variable> variables, Label label) {
        super(InstructionData.ZERO_VARIABLE, variables, label, InstructionType.SYNTHETIC, 1);
    }

    @Override
    public Label execute(ExecutionContext context) {

        context.updateVariable(getVariables().get(0), 0);

        return FixedLabel.EMPTY;
    }
}

