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


public class AssignmentInstruction extends AbstractInstruction implements ExpandableInstruction {

    private final Variable assignedVariable;

    public AssignmentInstruction(Variable variable, long instructionNumber, Variable assignedVariable, Instruction parent) {
        super(InstructionData.ASSIGNMENT, variable, InstructionType.SYNTHETIC, 2, instructionNumber, parent);
        this.assignedVariable = assignedVariable;
    }

    public AssignmentInstruction(Variable variable, Label label, long instructionNumber, Variable assignedVariable, Instruction parent) {
        super(InstructionData.ASSIGNMENT, variable, label, InstructionType.SYNTHETIC, 2, instructionNumber, parent);
        this.assignedVariable = assignedVariable;
    }

    @Override
    public Label execute(ExecutionContext context) {

        long assignedValue = context.getVariableValue(assignedVariable);
        context.updateVariable(getVariable(), assignedValue);

        return FixedLabel.EMPTY;
    }

    @Override
    public String getInstructionDescription(){
        return (getVariable().getRepresentation() + " <- " + assignedVariable.getRepresentation());
    }

    public Variable getAssignedVariable() {
        return assignedVariable;
    }

    @Override
    public List<Variable> getAllVariables(){
        List<Variable> allVariables = new ArrayList<>();
        allVariables.add(this.getVariable());
        allVariables.add(assignedVariable);
        return allVariables;
    }

    @Override
    public List<Instruction> expand(Set<Integer> zUsedNumbers, Set<Integer> usedLabelsNumbers, long instructionNumber){
        List<Instruction> nextInstructions = new ArrayList<>();
        Instruction newInstruction;
        Variable variable, assignedVariable;
        int labelNumber1, labelNumber2, labelNumber3;
        Label label1, label2, label3;
        int availableZnumber;
        Variable availableZvariable;

        variable = getVariable(); //V
        assignedVariable = this.assignedVariable; //V'

        newInstruction = new ZeroVariableInstruction(variable, this.getLabel(), instructionNumber, this); //V<-0
        nextInstructions.add(newInstruction);
        instructionNumber++;

        labelNumber1 = ExpansionUtils.findAvailableLabelNumber(usedLabelsNumbers); //L1
        usedLabelsNumbers.add(labelNumber1);
        label1 = new LabelImpl(labelNumber1);

        newInstruction = new JumpNotZeroInstruction(assignedVariable, label1, instructionNumber, this); //    IF V'!=0 GOTO L1
        nextInstructions.add(newInstruction);
        instructionNumber++;

        labelNumber3 = ExpansionUtils.findAvailableLabelNumber(usedLabelsNumbers); //L3
        usedLabelsNumbers.add(labelNumber3);
        label3 = new LabelImpl(labelNumber3);

        newInstruction = new GoToLabelInstruction(null, label3, instructionNumber, this); //GOTO L3
        nextInstructions.add(newInstruction);
        instructionNumber++;

        newInstruction = new DecreaseInstruction(assignedVariable, label1, instructionNumber, this); //L1 V'<-V'-1
        nextInstructions.add(newInstruction);
        instructionNumber++;

        availableZnumber = ExpansionUtils.findAvailableZNumber(zUsedNumbers);
        zUsedNumbers.add(availableZnumber);
        availableZvariable = new VariableImpl(VariableType.WORK, availableZnumber); //z1

        newInstruction = new IncreaseInstruction(availableZvariable, instructionNumber, this); //z1<-z1+1
        nextInstructions.add(newInstruction);
        instructionNumber++;

        newInstruction = new JumpNotZeroInstruction(assignedVariable, label1, instructionNumber, this); //IF V'!=0 GOTO L1
        nextInstructions.add(newInstruction);
        instructionNumber++;

        labelNumber2 = ExpansionUtils.findAvailableLabelNumber(usedLabelsNumbers);
        usedLabelsNumbers.add(labelNumber2);
        label2 = new LabelImpl(labelNumber2); //L2

        newInstruction = new DecreaseInstruction(availableZvariable, label2, instructionNumber, this); //L2 z1<-z1-1
        nextInstructions.add(newInstruction);
        instructionNumber++;

        newInstruction = new IncreaseInstruction(variable, instructionNumber, this); //V<-V+1
        nextInstructions.add(newInstruction);
        instructionNumber++;

        newInstruction = new IncreaseInstruction(assignedVariable, instructionNumber, this); //V'<-V'+1
        nextInstructions.add(newInstruction);
        instructionNumber++;

        newInstruction = new JumpNotZeroInstruction(availableZvariable, label2, instructionNumber, this); //IF z1!=0 GOTO L2
        nextInstructions.add(newInstruction);
        instructionNumber++;

        newInstruction = new NeutralInstruction(variable, label3, instructionNumber, this); //L3 V<-V
        nextInstructions.add(newInstruction);

        return nextInstructions;
    }
}