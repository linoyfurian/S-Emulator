package semulator.logic.instruction;

import semulator.logic.execution.ExecutionContext;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.variable.Variable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ConstantAssignmentInstruction extends AbstractInstruction implements ExpandableInstruction {

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
        return (getVariable().getRepresentation() + " <- " + constantValue);
    }

    long getConstantValue() {
        return constantValue;
    }

    @Override
    public List<Instruction> expand(Set<Integer> zUsedNumbers, Set<Integer> usedLabelsNumbers, long instructionNumber) {
        List<Instruction> nextInstructions = new ArrayList<>();
        long k = this.constantValue;
        Variable variable = getVariable();
        Instruction newInstruction;

        newInstruction = new ZeroVariableInstruction(variable, this.getLabel(), instructionNumber);
        nextInstructions.add(newInstruction);
        instructionNumber++;

        for (long i = 0; i < k; ++i) {
            newInstruction = new IncreaseInstruction(variable, instructionNumber);
            nextInstructions.add(newInstruction);
            instructionNumber++;
        }
        return nextInstructions;
    }
}
