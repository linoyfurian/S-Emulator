package semulator.logic.instruction;

import semulator.logic.execution.ExecutionContext;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.variable.Variable;

public class NeutralInstruction extends AbstractInstruction {

    public NeutralInstruction(Variable variable, long instructionNumber) {
        super(InstructionData.NEUTRAL, variable, InstructionType.BASIC, 0, instructionNumber);
    }

    public NeutralInstruction(Variable variable, Label label, long instructionNumber) {
        super(InstructionData.NEUTRAL, variable, label, InstructionType.BASIC, 0, instructionNumber);
    }

    @Override
    public Label execute(ExecutionContext context) {
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
        String variableRepresentation=getVariable().getRepresentation();
        return (variableRepresentation + " <- " + variableRepresentation);
    }
}