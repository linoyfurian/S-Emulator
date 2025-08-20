package semulator.logic.instruction;

import java.util.ArrayList;
import java.util.List;

public class ParentInstructionDto {
    private final String label;
    private final String command;
    private final char type;
    private final long number;
    private final int cycles;

    public ParentInstructionDto(Instruction instruction) {
        this.label = instruction.getLabel().getLabelRepresentation();
        this.command = instruction.getInstructionDescription();
        this.type = instruction.getType().getType();
        this.number = instruction.getInstructionNumber();
        this.cycles = instruction.cycles();
    }
}