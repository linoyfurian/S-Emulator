package semulator.logic.instruction;

import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.variable.Variable;

import java.util.List;

public abstract class AbstractInstruction implements Instruction {

    private final InstructionData instructionData;
    private final Label label;
    private final List<Variable> variables;

    public AbstractInstruction(InstructionData instructionData, List<Variable> variables) {
        this(instructionData, variables, FixedLabel.EMPTY);
    }

    public AbstractInstruction(InstructionData instructionData, List<Variable> variables, Label label) {
        this.instructionData = instructionData;
        this.label = label;
        this.variables = variables;
    }

    @Override
    public String getName() {
        return instructionData.getName();
    }

    @Override
    public int cycles() {
        return instructionData.getCycles();
    }

    @Override
    public Label getLabel() {
        return label;
    }

    @Override
    public List<Variable> getVariables() {
        return variables;
    }
}
