package semulator.logic.instruction;

import semulator.logic.execution.ExecutionContext;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.variable.Variable;

public class NeutralInstruction extends AbstractInstruction implements UnexpandableInstruction {

    public NeutralInstruction(Variable variable, long instructionNumber, Instruction parent) {
        super(InstructionData.NEUTRAL, variable, InstructionType.BASIC, 0, instructionNumber, parent);
    }

    public NeutralInstruction(Variable variable, Label label, long instructionNumber, Instruction parent) {
        super(InstructionData.NEUTRAL, variable, label, InstructionType.BASIC, 0, instructionNumber, parent);
    }

    @Override
    public Label execute(ExecutionContext context) {
        return FixedLabel.EMPTY;
    }

    @Override
    public String getInstructionDescription() {
        String variableRepresentation=getVariable().getRepresentation();
        return (variableRepresentation + " <- " + variableRepresentation);
    }

    @Override
    public Instruction cloneInstructionWithNewNumber(long number){
        Instruction newInstruction = new NeutralInstruction(getVariable(), getLabel(), number, null);
        return newInstruction;
    }
}