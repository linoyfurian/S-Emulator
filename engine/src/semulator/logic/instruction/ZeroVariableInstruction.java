package semulator.logic.instruction;

import semulator.logic.execution.ExecutionContext;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.variable.Variable;

import java.util.ArrayList;
import java.util.List;

public class ZeroVariableInstruction extends AbstractInstruction implements ExpandableInstruction {
    public ZeroVariableInstruction(Variable variable, long instructionNumber) {
        super(InstructionData.ZERO_VARIABLE, variable, InstructionType.SYNTHETIC, 1, instructionNumber);
    }

    public ZeroVariableInstruction(Variable variable, Label label, long instructionNumber) {
        super(InstructionData.ZERO_VARIABLE, variable, label, InstructionType.SYNTHETIC, 1, instructionNumber);
    }

    @Override
    public Label execute(ExecutionContext context) {

        context.updateVariable(getVariable(), 0);

        return FixedLabel.EMPTY;
    }

    @Override
    public String toString() {
        return String.format(
                "#%d (%s) [%s] %s (%d)",
                getInstructionNumber(),
                getType().getType(),
                getLabel().getLabelRepresentation(),
                getInstructionDescription(),
                cycles());
    }

    @Override
    public String getInstructionDescription() {
        return (getVariable().getRepresentation() + " <- 0");
    }

    @Override
    public List<Instruction> expand(){
        List<Instruction> nextInstructions = new ArrayList<>();

        return nextInstructions;
    }
}