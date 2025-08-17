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

public class JumpEqualConstantInstruction extends AbstractInstruction implements JumpInstruction, ExpandableInstruction{

    private final long constantValue;
    private final Label JEConstantLabel;

    public JumpEqualConstantInstruction(Variable variable, long constantValue, Label JEConstantLabel, long instructionNumber) {
        super(InstructionData.JUMP_EQUAL_CONSTANT, variable, InstructionType.SYNTHETIC, 3, instructionNumber);
        this.constantValue = constantValue;
        this.JEConstantLabel = JEConstantLabel;
    }

    public JumpEqualConstantInstruction(Variable variable, Label label, long constantValue, Label JEConstantLabel, long instructionNumber) {
        super(InstructionData.JUMP_EQUAL_CONSTANT, variable, label, InstructionType.SYNTHETIC, 3, instructionNumber);
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
        availableZVariable = new VariableImpl(VariableType.WORK, availableZNumber);

        newInstruction = new AssignmentInstruction(availableZVariable, this.getLabel(), instructionNumber, this.getVariable()); //z1<-V
        nextInstructions.add(newInstruction);
        instructionNumber++;

        labelNumber1 = ExpansionUtils.findAvailableLabelNumber(usedLabelsNumbers); //L1
        usedLabelsNumbers.add(labelNumber1);
        label1 = new LabelImpl(labelNumber1);

        k = this.constantValue;

        for (long i = 0; i < k; ++i) {
            newInstruction = new JumpZeroInstruction(availableZVariable, label1, instructionNumber); //IF z1=0 GOTO L1
            nextInstructions.add(newInstruction);
            instructionNumber++;

            newInstruction = new DecreaseInstruction(availableZVariable, instructionNumber); //z1<-z1-1
            nextInstructions.add(newInstruction);
            instructionNumber++;
        }

        newInstruction = new JumpNotZeroInstruction(availableZVariable, label1, instructionNumber); //IF z1!=0 GOTO L1
        nextInstructions.add(newInstruction);
        instructionNumber++;

        newInstruction = new GoToLabelInstruction(null, this.JEConstantLabel, instructionNumber); //GOTO L
        nextInstructions.add(newInstruction);
        instructionNumber++;

        newInstruction = new NeutralInstruction(Variable.RESULT, label1, instructionNumber); // L1 y<-y
        nextInstructions.add(newInstruction);

        return nextInstructions;
    }
}