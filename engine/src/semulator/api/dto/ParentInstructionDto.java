package semulator.api.dto;

import semulator.logic.instruction.ComplexInstruction;
import semulator.logic.instruction.Instruction;
import semulator.logic.instruction.SimpleInstruction;
import semulator.logic.program.Program;
import semulator.logic.program.ProgramImpl;

public class ParentInstructionDto {
    private final String label;
    private final String command;
    private final char type;
    private final long number;
    private final int cycles;

    public ParentInstructionDto(Instruction instruction, Program program) {
        this.label = instruction.getLabel().getLabelRepresentation();
        if(instruction instanceof SimpleInstruction simpleInstruction)
            this.command = simpleInstruction.getInstructionDescription();
        else {
            if(instruction instanceof ComplexInstruction complexInstruction)
                this.command = complexInstruction.getInstructionDescription(program);
            else
                this.command = "";
        }
        this.type = instruction.getType().getType();
        this.number = instruction.getInstructionNumber();
        this.cycles = instruction.cycles();
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
}