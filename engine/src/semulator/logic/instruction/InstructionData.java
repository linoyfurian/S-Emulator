package semulator.logic.instruction;

public enum InstructionData {

    INCREASE("INCREASE", 1),
    DECREASE("DECREASE", 1),
    NO_OP("NO_OP", 0),
    JUMP_NOT_ZERO("JNZ", 3),
    ZERO_VARIABLE("ZERO_VARIABLE", 1),
    GOTO_LABEL("GOTO_LABEL", 1),
    ASSIGNMENT("ASSIGNMENT", 4)

    ;

    private final String name;
    private final int cycles;

    InstructionData(String name, int cycles) {
        this.name = name;
        this.cycles = cycles;
    }

    public String getName() {
        return name;
    }

    public int getCycles() {
        return cycles;
    }
}
