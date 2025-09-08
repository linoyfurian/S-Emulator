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
import java.util.Map;
import java.util.Set;

public class JumpEqualConstantInstruction extends AbstractInstruction implements JumpInstruction, ExpandableInstruction, SimpleInstruction {

    private final long constantValue;
    private final Label JEConstantLabel;

    public JumpEqualConstantInstruction(Variable variable, long constantValue, Label JEConstantLabel, long instructionNumber,  Instruction parent) {
        super(InstructionData.JUMP_EQUAL_CONSTANT, variable, InstructionType.SYNTHETIC, 3, instructionNumber, parent);
        this.constantValue = constantValue;
        this.JEConstantLabel = JEConstantLabel;
    }

    public JumpEqualConstantInstruction(Variable variable, Label label, long constantValue, Label JEConstantLabel, long instructionNumber, Instruction parent) {
        super(InstructionData.JUMP_EQUAL_CONSTANT, variable, label, InstructionType.SYNTHETIC, 3, instructionNumber, parent);
        this.constantValue = constantValue;
        this.JEConstantLabel = JEConstantLabel;
    }


    @Override
    public Label execute(ExecutionContext context) {

        long variableValue = context.getVariableValue(getVariable());
        if(variableValue == constantValue)
            return JEConstantLabel;

        return FixedLabel.EMPTY;
    }

    @Override
    public String getInstructionDescription() {
        return ("IF " + getVariable().getRepresentation() + "=" + constantValue + " GOTO " + JEConstantLabel.getLabelRepresentation());
    }

    @Override
    public Label getTargetLabel() {
        return JEConstantLabel;
    }

    @Override
    public List<Label> getAllLabels(){
        List<Label> allLabels = new ArrayList<>();
        allLabels.add(this.getLabel());
        allLabels.add(JEConstantLabel);
        return allLabels;
    }

    @Override
    public List<Instruction> expand(Set<Integer> zUsedNumbers, Set<Integer> usedLabelsNumbers, long instructionNumber) {
        List<Instruction> nextInstructions = new ArrayList<>();
        int labelNumber1;
        Label label1;
        int availableZNumber;
        long k = this.constantValue;
        Variable availableZVariable;
        Instruction newInstruction;

        availableZNumber = ExpansionUtils.findAvailableZNumber(zUsedNumbers); //z1
        zUsedNumbers.add(availableZNumber);
        String zVariableName = "z" + availableZNumber;
        availableZVariable = new VariableImpl(VariableType.WORK, availableZNumber, zVariableName);

        newInstruction = new AssignmentInstruction(availableZVariable, this.getLabel(), instructionNumber, this.getVariable(), this); //z1<-V
        nextInstructions.add(newInstruction);
        instructionNumber++;

        labelNumber1 = ExpansionUtils.findAvailableLabelNumber(usedLabelsNumbers); //L1
        usedLabelsNumbers.add(labelNumber1);
        label1 = new LabelImpl(labelNumber1);

        for (long i = 0; i < k; ++i) {
            newInstruction = new JumpZeroInstruction(availableZVariable, label1, instructionNumber, this); //IF z1=0 GOTO L1
            nextInstructions.add(newInstruction);
            instructionNumber++;

            newInstruction = new DecreaseInstruction(availableZVariable, instructionNumber, this); //z1<-z1-1
            nextInstructions.add(newInstruction);
            instructionNumber++;
        }

        newInstruction = new JumpNotZeroInstruction(availableZVariable, label1, instructionNumber, this); //IF z1!=0 GOTO L1
        nextInstructions.add(newInstruction);
        instructionNumber++;

        newInstruction = new GoToLabelInstruction(null, this.JEConstantLabel, instructionNumber,this); //GOTO L
        nextInstructions.add(newInstruction);
        instructionNumber++;

        newInstruction = new NeutralInstruction(Variable.RESULT, label1, instructionNumber, this); // L1 y<-y
        nextInstructions.add(newInstruction);

        return nextInstructions;
    }

    @Override
    public Instruction QuoteFunctionExpandHelper(Set<Integer> zUsedNumbers, Set<Integer> usedLabelsNumbers, long instructionNumber, Map<String, String> oldAndNew, Instruction parent){
        Instruction newInstruction;
        Label label, newLabelImpl, newJEConstantLabel;
        Variable variable, newVariableImpl;

        //check label
        label = this.getLabel();
        newLabelImpl = ExpansionUtils.validateOrCreateLabel(label, usedLabelsNumbers, oldAndNew);

        //check JEC label
        newJEConstantLabel = ExpansionUtils.validateOrCreateLabel(this.JEConstantLabel, usedLabelsNumbers, oldAndNew);

        //check variable
        variable = this.getVariable();
        newVariableImpl = ExpansionUtils.validateOrCreateVariable(variable, zUsedNumbers, oldAndNew);

        newInstruction = new JumpEqualConstantInstruction(newVariableImpl, newLabelImpl, this.constantValue, newJEConstantLabel, instructionNumber, parent);

        return newInstruction;
    }

    @Override
    public Instruction cloneWithDifferentNumber(long number){
        Instruction newInstruction;
        newInstruction = new JumpEqualConstantInstruction(this.getVariable(),this.getLabel(), this.constantValue, this.JEConstantLabel, number, this.getParent());
        return newInstruction;
    }
}