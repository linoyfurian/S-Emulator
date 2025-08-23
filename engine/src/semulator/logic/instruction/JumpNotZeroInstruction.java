package semulator.logic.instruction;

import semulator.logic.execution.ExecutionContext;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.variable.Variable;

import java.util.ArrayList;
import java.util.List;

public class JumpNotZeroInstruction extends AbstractInstruction implements JumpInstruction, UnexpandableInstruction{

    private final Label JNZLabel;

    public JumpNotZeroInstruction(Variable variable, Label JNZLabel, long instructionNumber, Instruction parent) {
        super(InstructionData.JUMP_NOT_ZERO, variable, InstructionType.BASIC, 0, instructionNumber, parent);
        this.JNZLabel = JNZLabel;
    }

    public JumpNotZeroInstruction(Variable variable, Label JNZLabel, Label label, long instructionNumber, Instruction parent) {
        super(InstructionData.JUMP_NOT_ZERO, variable, label, InstructionType.BASIC, 0, instructionNumber, parent);
        this.JNZLabel = JNZLabel;
    }

    @Override
    public Label execute(ExecutionContext context) {
        long variableValue = context.getVariableValue(getVariable());

        if (variableValue != 0) {
            return JNZLabel;
        }
        return FixedLabel.EMPTY;
    }

    @Override
    public String getInstructionDescription() {
        return ("IF " + getVariable().getRepresentation() + "!=0" + " GOTO " + JNZLabel.getLabelRepresentation());
    }

    @Override
    public Label getTargetLabel() {
        return JNZLabel;
    }

    @Override
    public List<Label> getAllLabels(){
        List<Label> allLabels = new ArrayList<>();
        allLabels.add(this.getLabel());
        allLabels.add(JNZLabel);
        return allLabels;
    }

    @Override
    public Instruction cloneInstructionWithNewNumber(long number){
        Instruction newInstruction = new JumpNotZeroInstruction(getVariable(), this.JNZLabel, getLabel(), number, this.getParent());
        return newInstruction;
    }
}