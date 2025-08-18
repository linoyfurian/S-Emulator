package semulator.logic.instruction;

public interface UnexpandableInstruction {
    Instruction cloneInstructionWithNewNumber(long number);
}
