package semulator.logic.instruction;

import java.util.List;

public interface ExpandableInstruction {
    List<Instruction> expand();
}
