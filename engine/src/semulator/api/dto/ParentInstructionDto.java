package semulator.api.dto;

import semulator.logic.instruction.ComplexInstruction;
import semulator.logic.instruction.Instruction;
import semulator.logic.instruction.SimpleInstruction;
import semulator.logic.program.Program;
import semulator.logic.program.ProgramImpl;

import java.util.ArrayList;

public class ParentInstructionDto {
    private final String label;
    private final String command;
    private final char type;
    private final long number;
    private final String cycles;

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

        Integer cyclesValue = instruction.cycles();
        if(instruction instanceof ComplexInstruction)
            this.cycles = "X+" + cyclesValue;
        else
            this.cycles = cyclesValue.toString();
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
}