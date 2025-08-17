package semulator.logic.instruction;

import semulator.logic.execution.ExecutionContext;
import semulator.logic.instruction.expansion.ExpansionUtils;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.label.LabelImpl;
import semulator.logic.variable.Variable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class JumpZeroInstruction extends AbstractInstruction implements JumpInstruction, ExpandableInstruction{

    private final Label jzLabel;

    public JumpZeroInstruction(Variable variable, Label jzLabel, long instructionNumber) {
        super(InstructionData.JUMP_ZERO, variable, InstructionType.SYNTHETIC, 2, instructionNumber);
        this.jzLabel = jzLabel;
    }

    public JumpZeroInstruction(Variable variable, Label jzLabel, Label label, long instructionNumber) {
        super(InstructionData.JUMP_ZERO, variable, label, InstructionType.SYNTHETIC, 2, instructionNumber);
        this.jzLabel = jzLabel;
    }

    @Override
    public Label execute(ExecutionContext context) {
        long variableValue = context.getVariableValue(getVariable());

        if (variableValue == 0) {
            return jzLabel;
        }
        return FixedLabel.EMPTY;
    }

    @Override
    public String toString() {
        return String.format(
                "#%d (%s) [%s] %s (%d)",
                getInstructionNumber(),
                getType().getType(),
                getLabel().getLabelRepresentation(),
                getInstructionDescription(),
                cycles());
    }

    @Override
    public String getInstructionDescription() {
        return ("IF " + getVariable().getRepresentation() + "=0" + " GOTO " + jzLabel.getLabelRepresentation());
    }

    @Override
    public Label getTargetLabel() {
        return jzLabel;
    }

    @Override
    public List<Label> getAllLabels(){
        List<Label> allLabels = new ArrayList<>();
        allLabels.add(this.getLabel());
        allLabels.add(jzLabel);
        return allLabels;
    }


    @Override
    public List<Instruction> expand(Set<Integer> zUsedNumbers, Set<Integer> usedLabelsNumbers, long instructionNumber) {
        List<Instruction> nextInstructions = new ArrayList<>();
        int labelNumber1;
        Label label1;
        Instruction newInstruction;

        labelNumber1= ExpansionUtils.findAvailableLabelNumber(usedLabelsNumbers); //L1
        usedLabelsNumbers.add(labelNumber1);
        label1 = new LabelImpl(labelNumber1);

        newInstruction = new JumpNotZeroInstruction(getVariable(), label1, getLabel(), instructionNumber); //IF V!=0 GOTO L1
        nextInstructions.add(newInstruction);
        instructionNumber++;

        newInstruction = new GoToLabelInstruction(null, getTargetLabel(), instructionNumber); //GOTO L
        nextInstructions.add(newInstruction);
        instructionNumber++;

        newInstruction = new NeutralInstruction(Variable.RESULT, label1, instructionNumber); //L1 y<-y
        nextInstructions.add(newInstruction);

        return nextInstructions;
    }
}