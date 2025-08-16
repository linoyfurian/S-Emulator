package semulator.logic.instruction;

import semulator.logic.label.Label;

public interface JumpInstruction {
    Label getTargetLabel();
}
