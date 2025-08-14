package semulator.logic.instruction;

import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.variable.Variable;

public abstract class AbstractInstruction implements Instruction {

    private final InstructionData instructionData;
    private final Label label;
    private final Variable variable;
    private final InstructionType type;
    private final int degreeOfExpansion;
    private final long instructionNumber;

    public AbstractInstruction(InstructionData instructionData, Variable variable, InstructionType type, int degreeOfExpansion, long instructionNumber) {
        this(instructionData, variable, FixedLabel.EMPTY, type, degreeOfExpansion, instructionNumber);
    }

    public AbstractInstruction(InstructionData instructionData, Variable variable, Label label, InstructionType type, int degreeOfExpansion, long instructionNumber) {
        this.instructionData = instructionData;
        this.label = label;
        this.variable = variable;
        this.type = type;
        this.degreeOfExpansion = degreeOfExpansion;
        this.instructionNumber = instructionNumber;
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
    public Variable getVariable() {
        return variable;
    }

}
