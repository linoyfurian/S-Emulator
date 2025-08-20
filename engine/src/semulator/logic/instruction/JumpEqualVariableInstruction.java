package semulator.logic.instruction;

import semulator.logic.execution.ExecutionContext;
import semulator.logic.instruction.expansion.ExpansionUtils;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.label.LabelImpl;
import semulator.logic.variable.Variable;
import semulator.logic.variable.VariableImpl;
import semulator.logic.variable.VariableType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class JumpEqualVariableInstruction extends AbstractInstruction implements JumpInstruction, ExpandableInstruction{

    private final Label JEVariableLabel;
    private final Variable variableName;

    public JumpEqualVariableInstruction(Variable variable, Label JEVariableLabel, Variable variableName, long instructionNumber, Instruction parent) {
        super(InstructionData.JUMP_EQUAL_VARIABLE, variable, InstructionType.SYNTHETIC, 3, instructionNumber, parent);
        this.JEVariableLabel = JEVariableLabel;
        this.variableName = variableName;
    }

    public JumpEqualVariableInstruction(Variable variable, Label label, Label JEVariableLabel, Variable variableName, long instructionNumber, Instruction parent) {
        super(InstructionData.JUMP_EQUAL_VARIABLE, variable, label, InstructionType.SYNTHETIC, 3, instructionNumber, parent);
        this.JEVariableLabel = JEVariableLabel;
        this.variableName = variableName;
    }

    @Override
    public Label execute(ExecutionContext context) {
        long firstVariableValue = context.getVariableValue(getVariable());
        long secondVariableValue = context.getVariableValue(variableName);

        if (firstVariableValue == secondVariableValue)
            return JEVariableLabel;

        return FixedLabel.EMPTY;
    }

    @Override
    public String getInstructionDescription() {
        return ("IF " + getVariable().getRepresentation() + "=" + variableName.getRepresentation() + " GOTO " + JEVariableLabel.getLabelRepresentation());
    }

    @Override
    public Label getTargetLabel() {
        return JEVariableLabel;
    }

    public Variable getVariableName() {
        return variableName;
    }

    @Override
    public List<Variable> getAllVariables(){
        List<Variable> allVariables = new ArrayList<>();
        allVariables.add(this.getVariable());
        allVariables.add(variableName);
        return allVariables;
    }

    @Override
    public List<Label> getAllLabels(){
        List<Label> allLabels = new ArrayList<>();
        allLabels.add(this.getLabel());
        allLabels.add(JEVariableLabel);
        return allLabels;
    }

    @Override
    public List<Instruction> expand(Set<Integer> zUsedNumbers, Set<Integer> usedLabelsNumbers, long instructionNumber) {
        List<Instruction> nextInstructions = new ArrayList<>();
        int labelNumber1, labelNumber2, labelNumber3;
        Label label1,  label2, label3;
        int availableZNumber1, availableZNumber2;
        Variable availableZVariable1, availableZVariable2;
        Instruction newInstruction;

        availableZNumber1 = ExpansionUtils.findAvailableZNumber(zUsedNumbers); //z1
        zUsedNumbers.add(availableZNumber1);
        availableZVariable1 = new VariableImpl(VariableType.WORK, availableZNumber1);

        newInstruction = new AssignmentInstruction(availableZVariable1, this.getLabel(), instructionNumber, this.getVariable(), this); //z1<-V
        nextInstructions.add(newInstruction);
        instructionNumber++;

        availableZNumber2 = ExpansionUtils.findAvailableZNumber(zUsedNumbers); //z2
        zUsedNumbers.add(availableZNumber2);
        availableZVariable2 = new VariableImpl(VariableType.WORK, availableZNumber2);

        newInstruction = new AssignmentInstruction(availableZVariable2, instructionNumber, this.variableName, this); //z2<-V'
        nextInstructions.add(newInstruction);
        instructionNumber++;

        labelNumber2 = ExpansionUtils.findAvailableLabelNumber(usedLabelsNumbers); //L2
        usedLabelsNumbers.add(labelNumber2);
        label2 = new LabelImpl(labelNumber2);

        labelNumber3 = ExpansionUtils.findAvailableLabelNumber(usedLabelsNumbers); //L3
        usedLabelsNumbers.add(labelNumber3);
        label3 = new LabelImpl(labelNumber3);

        newInstruction = new JumpZeroInstruction(availableZVariable1, label3, label2, instructionNumber, this); //L2 IF z1=0 GOTO L3
        nextInstructions.add(newInstruction);
        instructionNumber++;

        labelNumber1 = ExpansionUtils.findAvailableLabelNumber(usedLabelsNumbers); //L1
        usedLabelsNumbers.add(labelNumber1);
        label1 = new LabelImpl(labelNumber1);

        newInstruction = new JumpZeroInstruction(availableZVariable2, label1, instructionNumber, this); //IF z2=0 GOTO L1
        nextInstructions.add(newInstruction);
        instructionNumber++;

        newInstruction = new DecreaseInstruction(availableZVariable1, instructionNumber, this); //z1<-z1-1
        nextInstructions.add(newInstruction);
        instructionNumber++;

        newInstruction = new DecreaseInstruction(availableZVariable2, instructionNumber, this); //z2<-z2-1
        nextInstructions.add(newInstruction);
        instructionNumber++;

        newInstruction = new GoToLabelInstruction(null, label2, instructionNumber, this); //GOTO L2
        nextInstructions.add(newInstruction);
        instructionNumber++;

        newInstruction = new JumpZeroInstruction(availableZVariable2, this.JEVariableLabel, label3, instructionNumber, this); //L3 IF z2=0 GOTO L
        nextInstructions.add(newInstruction);
        instructionNumber++;

        newInstruction = new NeutralInstruction(Variable.RESULT, label1, instructionNumber, this); //L1 y<-y
        nextInstructions.add(newInstruction);

        return nextInstructions;
    }
}