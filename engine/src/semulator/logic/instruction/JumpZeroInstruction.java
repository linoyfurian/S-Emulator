package semulator.logic.instruction;

import semulator.logic.execution.ExecutionContext;
import semulator.logic.instruction.expansion.ExpansionUtils;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.label.LabelImpl;
import semulator.logic.variable.Variable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JumpZeroInstruction extends AbstractInstruction implements JumpInstruction, ExpandableInstruction, SimpleInstruction {

    private final Label JZLabel;

    public JumpZeroInstruction(Variable variable, Label JZLabel, long instructionNumber, Instruction parent) {
        super(InstructionData.JUMP_ZERO, variable, InstructionType.SYNTHETIC, 2, instructionNumber, parent);
        this.JZLabel = JZLabel;
    }

    public JumpZeroInstruction(Variable variable, Label JZLabel, Label label, long instructionNumber, Instruction parent) {
        super(InstructionData.JUMP_ZERO, variable, label, InstructionType.SYNTHETIC, 2, instructionNumber, parent);
        this.JZLabel = JZLabel;
    }

    @Override
    public Label execute(ExecutionContext context) {
        long variableValue = context.getVariableValue(getVariable());

        if (variableValue == 0) {
            return JZLabel;
        }
        return FixedLabel.EMPTY;
    }

    @Override
    public String getInstructionDescription() {
        return ("IF " + getVariable().getRepresentation() + "=0" + " GOTO " + JZLabel.getLabelRepresentation());
    }

    @Override
    public Label getTargetLabel() {
        return JZLabel;
    }

    @Override
    public List<Label> getAllLabels(){
        List<Label> allLabels = new ArrayList<>();
        allLabels.add(this.getLabel());
        allLabels.add(JZLabel);
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

        newInstruction = new JumpNotZeroInstruction(this.getVariable(), label1, this.getLabel(), instructionNumber, this); //IF V!=0 GOTO L1
        nextInstructions.add(newInstruction);
        instructionNumber++;

        newInstruction = new GoToLabelInstruction(null, this.getTargetLabel(), instructionNumber, this); //GOTO L
        nextInstructions.add(newInstruction);
        instructionNumber++;

        newInstruction = new NeutralInstruction(Variable.RESULT, label1, instructionNumber, this); //L1 y<-y
        nextInstructions.add(newInstruction);

        return nextInstructions;
    }


    @Override
    public Instruction QuoteFunctionExpandHelper(Set<Integer> zUsedNumbers, Set<Integer> usedLabelsNumbers, long instructionNumber, Map<String, String> oldAndNew, Instruction parent){
        Instruction newInstruction;
        Label label, newLabelImpl, newJZLabel;
        Variable variable, newVariableImpl;

        //check label
        label = this.getLabel();
        newLabelImpl = ExpansionUtils.validateOrCreateLabel(label, usedLabelsNumbers, oldAndNew);

        //check variable
        variable = this.getVariable();
        newVariableImpl = ExpansionUtils.validateOrCreateVariable(variable, zUsedNumbers, oldAndNew);

        //check JZ label
        newJZLabel = ExpansionUtils.validateOrCreateLabel(this.JZLabel, usedLabelsNumbers, oldAndNew);

        newInstruction = new JumpZeroInstruction(newVariableImpl, newJZLabel, newLabelImpl, instructionNumber, parent);

        return newInstruction;
    }

    @Override
    public Instruction cloneWithDifferentNumber(long number){
        Instruction newInstruction;
        newInstruction = new JumpZeroInstruction(this.getVariable(),this.JZLabel, this.getLabel(), number, this.getParent());
        return newInstruction;
    }
}