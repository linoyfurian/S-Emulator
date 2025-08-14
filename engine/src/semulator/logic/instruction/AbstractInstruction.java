package semulator.logic.instruction;

import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.variable.Variable;

import java.util.List;

public abstract class AbstractInstruction implements Instruction {

    private final InstructionData instructionData;
    private final Label label;
    private final List<Variable> variables;
    private final InstructionType type;
    private final int degreeOfExpansion;

    public AbstractInstruction(InstructionData instructionData, List<Variable> variables, InstructionType type, int degreeOfExpansion) {
        this(instructionData, variables, FixedLabel.EMPTY, type, degreeOfExpansion);
    }

    public AbstractInstruction(InstructionData instructionData, List<Variable> variables, Label label, InstructionType type, int degreeOfExpansion) {
        this.instructionData = instructionData;
        this.label = label;
        this.variables = variables;
        this.type = type;
        this.degreeOfExpansion = degreeOfExpansion;
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
    public int getExpansionDegree(){
        return degreeOfExpansion;
    }

    @Override
    public List<Variable> getVariables() {
        return variables;
    }
}
