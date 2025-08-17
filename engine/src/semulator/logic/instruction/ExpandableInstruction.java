package semulator.logic.instruction;

import java.util.List;
import java.util.Set;

public interface ExpandableInstruction {
    List<Instruction> expand(Set<Integer> zUsedNumbers, Set<Integer> usedLabelsNumbers, long instructionNumber);
}
