package semulator.logic.instruction;

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