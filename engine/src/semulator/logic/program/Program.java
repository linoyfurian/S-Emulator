package semulator.logic.program;

import semulator.logic.instruction.Instruction;
import semulator.logic.variable.Variable;

import java.util.List;

public interface Program {

    String getName();
    void addInstruction(Instruction instruction);
    List<Instruction> getInstructions();
    boolean validate();
    int calculateMaxDegree();
    int calculateCycles();
    List<V>
}
