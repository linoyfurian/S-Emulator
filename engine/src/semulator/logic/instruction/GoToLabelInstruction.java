package semulator.logic.instruction;

import semulator.logic.execution.ExecutionContext;
import semulator.logic.instruction.expansion.ExpansionUtils;
import semulator.logic.label.Label;
import semulator.logic.variable.Variable;
import semulator.logic.variable.VariableImpl;
import semulator.logic.variable.VariableType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GoToLabelInstruction extends AbstractInstruction implements JumpInstruction, ExpandableInstruction {

    private final Label gotoLabel;

    public GoToLabelInstruction(Variable variable, Label gotoLabel, long instructionNumber, Instruction parent) {
        super(InstructionData.GOTO_LABEL, variable, InstructionType.SYNTHETIC, 1, instructionNumber, parent);
        this.gotoLabel = gotoLabel;
    }

    public GoToLabelInstruction(Variable variable, Label label, Label gotoLabel, long instructionNumber, Instruction parent) {
        super(InstructionData.GOTO_LABEL, variable, label, InstructionType.SYNTHETIC, 1, instructionNumber, parent);
        this.gotoLabel = gotoLabel;
    }

    @Override
    public Label execute(ExecutionContext context) {
        return gotoLabel;
    }

    @Override
    public String getInstructionDescription() {
        return ("GOTO " + gotoLabel.getLabelRepresentation());
    }

    @Override
    public Label getTargetLabel() {
        return gotoLabel;
    }

    @Override
    public List<Label> getAllLabels(){
        List<Label> allLabels = new ArrayList<>();
        allLabels.add(this.getLabel());
        allLabels.add(gotoLabel);
        return allLabels;
    }

    @Override
    public List<Instruction> expand(Set<Integer> zUsedNumbers, Set<Integer> usedLabelsNumbers, long instructionNumber){
        List<Instruction> nextInstructions = new ArrayList<>();

        int newZ;
        newZ= ExpansionUtils.findAvailableZNumber(zUsedNumbers);
        zUsedNumbers.add(newZ);
        Variable newVariable = new VariableImpl(VariableType.WORK, newZ); //z1

        Instruction newInstruction = new IncreaseInstruction(newVariable, this.getLabel(), instructionNumber, this); //z1<-z1+1
        nextInstructions.add(newInstruction);
        instructionNumber++;

        Instruction newInstruction2 = new JumpNotZeroInstruction(newVariable, this.getTargetLabel(), instructionNumber, this); //IF z1!=0 GOTO L
        nextInstructions.add(newInstruction2);

        return nextInstructions;
    }
}