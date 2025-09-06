package semulator.api.dto;

import semulator.logic.instruction.Instruction;
import semulator.logic.label.Label;
import semulator.logic.variable.Variable;

import java.util.ArrayList;
import java.util.List;

public class InstructionDto {
    private final String label;
    private final String command;
    private final char type;
    private final long number;
    private final int cycles;
    private final List<ParentInstructionDto> parents;
    private final List<String> allLabels;
    private final List<String> allVariables;

    public InstructionDto(Instruction instruction) {
        this.label = instruction.getLabel().getLabelRepresentation();
        this.command = instruction.getInstructionDescription();
        this.type = instruction.getType().getType();
        this.number = instruction.getInstructionNumber();
        this.cycles = instruction.cycles();
        this.parents = new ArrayList<>();
        Instruction parent;
        parent = instruction.getParent();
        while(parent != null){
            this.parents.add(new ParentInstructionDto(parent));
            parent = parent.getParent();
        }

        List<Label> Labels = instruction.getAllLabels();
        this.allLabels = new ArrayList<>();

        for(Label label : Labels){
            if(label!=null)
                this.allLabels.add(label.getLabelRepresentation());
        }

        List<Variable> Variables = instruction.getAllVariables();
        this.allVariables = new ArrayList<>();

        for(Variable variable : Variables){
            if(variable!=null)
                this.allVariables.add(variable.getRepresentation());
        }

    }

    public long getNumber() {
        return number;
    }

    public char getType() {
        return type;
    }

    public String getLabel() {
        return label;
    }

    public String getCommand() {
        return command;
    }

    public int getCycles() {
        return cycles;
    }

    public List<ParentInstructionDto> getParents() {
        return parents;
    }

    public List<String> getAllLabels() {
        return allLabels;
    }

    public List<String> getAllVariables() {
        return allVariables;
    }
}