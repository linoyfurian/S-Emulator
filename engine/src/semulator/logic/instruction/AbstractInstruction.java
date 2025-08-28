package semulator.logic.instruction;

import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.variable.Variable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractInstruction implements Instruction, Serializable {

    private final InstructionData instructionData;
    private final Label label;
    private final Variable variable;
    private final InstructionType type;
    private final int degreeOfExpansion;
    private final long instructionNumber;
    private final Instruction parent;

    public AbstractInstruction(InstructionData instructionData, Variable variable, InstructionType type, int degreeOfExpansion, long instructionNumber, Instruction parent) {
        this(instructionData, variable, FixedLabel.EMPTY, type, degreeOfExpansion, instructionNumber, parent);
    }

    public AbstractInstruction(InstructionData instructionData, Variable variable, Label label, InstructionType type, int degreeOfExpansion, long instructionNumber,  Instruction parent) {
        this.instructionData = instructionData;
        this.label = label;
        this.variable = variable;
        this.type = type;
        this.degreeOfExpansion = degreeOfExpansion;
        this.instructionNumber = instructionNumber;
        this.parent = parent;
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

    @Override
    public long getInstructionNumber() {
        return instructionNumber;
    }

    @Override
    public InstructionType getType() {
        return type;
    }

    @Override
    public List<Variable> getAllVariables(){
        List<Variable> allVariables = new ArrayList<>();
        allVariables.add(variable);
        return allVariables;
    }

    @Override
    public List<Label> getAllLabels(){
        List<Label> allLabels = new ArrayList<>();
        allLabels.add(label);
        return allLabels;
    }

    @Override
    public Instruction getParent(){
        return parent;
    }
}