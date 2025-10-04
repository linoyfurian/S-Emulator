package semulator.api.dto;

import semulator.logic.instruction.*;
import semulator.logic.label.Label;
import semulator.logic.program.Program;
import semulator.logic.program.ProgramImpl;
import semulator.logic.variable.Variable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class InstructionDto {
    private final String label;
    private final String command;
    private final char type;
    private final long number;
    private final String cycles;
    private final List<ParentInstructionDto> parents;
    private final List<String> allLabels;
    private final List<String> allVariables;
    private final String mainVariable;
    private final boolean isJumpInstruction;

    public InstructionDto(Instruction instruction, Map<String, Program> functions) {
        this.label = instruction.getLabel().getLabelRepresentation();
        if(instruction instanceof SimpleInstruction simpleInstruction)
            this.command = simpleInstruction.getInstructionDescription();
        else {
            if(instruction instanceof ComplexInstruction complexInstruction){
                this.command = complexInstruction.getInstructionDescription(functions);
            }
            else
                this.command = "";
        }
        this.type = instruction.getType().getType();
        this.number = instruction.getInstructionNumber();

        Integer cyclesValue = instruction.cycles();
        if(instruction instanceof ComplexInstruction)
            this.cycles = "X+" + cyclesValue;
        else
            this.cycles = cyclesValue.toString();
        this.parents = new ArrayList<>();
        Instruction parent;
        parent = instruction.getParent();
        while(parent != null){
            this.parents.add(new ParentInstructionDto(parent, functions));
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

        Variable mainVar = instruction.getVariable();
        if(mainVar!=null)
            this.mainVariable = mainVar.getRepresentation();
        else
            this.mainVariable = "";

        if(instruction instanceof JumpInstruction){
            this.isJumpInstruction = true;
        }
        else{
            this.isJumpInstruction = false;
        }

    }

    public boolean isJumpInstruction() {
        return isJumpInstruction;
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

    public String getCycles() {
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

    public String getMainVariable() {
        return mainVariable;
    }
}