package semulator.logic.instruction;

import semulator.logic.execution.ExecutionContext;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.variable.Variable;

import java.util.ArrayList;
import java.util.List;

public class JumpNotZeroInstruction extends AbstractInstruction implements JumpInstruction, UnexpandableInstruction{

    private final Label jnzLabel;

    public JumpNotZeroInstruction(Variable variable, Label jnzLabel, long instructionNumber) {
        super(InstructionData.JUMP_NOT_ZERO, variable, InstructionType.BASIC, 0, instructionNumber);
        this.jnzLabel = jnzLabel;
    }

    public JumpNotZeroInstruction(Variable variable, Label jnzLabel, Label label, long instructionNumber) {
        super(InstructionData.JUMP_NOT_ZERO, variable, label, InstructionType.BASIC, 0, instructionNumber);
        this.jnzLabel = jnzLabel;
    }

    @Override
    public Label execute(ExecutionContext context) {
        long variableValue = context.getVariableValue(getVariable());

        if (variableValue != 0) {
            return jnzLabel;
        }
        return FixedLabel.EMPTY;
    }

    @Override
    public String getInstructionDescription() {
        return ("IF " + getVariable().getRepresentation() + "!=0" + " GOTO " + jnzLabel.getLabelRepresentation());
    }

    @Override
    public Label getTargetLabel() {
        return jnzLabel;
    }

    @Override
    public List<Label> getAllLabels(){
        List<Label> allLabels = new ArrayList<>();
        allLabels.add(this.getLabel());
        allLabels.add(jnzLabel);
        return allLabels;
    }

    @Override
    public Instruction cloneInstructionWithNewNumber(long number){
        Instruction newInstruction = new JumpNotZeroInstruction(getVariable(), this.jnzLabel, getLabel(), number);
        return newInstruction;
    }
}