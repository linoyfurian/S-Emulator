package semulator.logic.instruction;

import semulator.core.loader.XmlProgramMapperV2;
import semulator.logic.execution.ExecutionContext;
import semulator.logic.instruction.expansion.ExpansionUtils;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.label.LabelImpl;
import semulator.logic.variable.Variable;
import semulator.logic.variable.VariableType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConstantAssignmentInstruction extends AbstractInstruction implements ExpandableInstruction, SimpleInstruction {

    private final long constantValue;

    public ConstantAssignmentInstruction(Variable variable, long constantValue, long instructionNumber, Instruction parent) {
        super(InstructionData.CONSTANT_ASSIGNMENT, variable, InstructionType.SYNTHETIC, 2, instructionNumber, parent);
        this.constantValue = constantValue;
    }

    public ConstantAssignmentInstruction(Variable variable, Label label, long constantValue, long instructionNumber, Instruction parent) {
        super(InstructionData.CONSTANT_ASSIGNMENT, variable, label, InstructionType.SYNTHETIC, 2, instructionNumber, parent);
        this.constantValue = constantValue;
    }

    @Override
    public Label execute(ExecutionContext context) {

        context.updateVariable(getVariable(), constantValue);

        return FixedLabel.EMPTY;
    }

    @Override
    public String getInstructionDescription() {
        return (getVariable().getRepresentation() + " <- " + constantValue);
    }

    @Override
    public List<Instruction> expand(Set<Integer> zUsedNumbers, Set<Integer> usedLabelsNumbers, long instructionNumber) {
        List<Instruction> nextInstructions = new ArrayList<>();
        long k = this.constantValue;
        Variable variable = getVariable();
        Instruction newInstruction;

        newInstruction = new ZeroVariableInstruction(variable, this.getLabel(), instructionNumber, this);
        nextInstructions.add(newInstruction);
        instructionNumber++;

        for (long i = 0; i < k; ++i) {
            newInstruction = new IncreaseInstruction(variable, instructionNumber, this);
            nextInstructions.add(newInstruction);
            instructionNumber++;
        }
        return nextInstructions;
    }

    @Override
    public Instruction QuoteFunctionExpandHelper(Set<Integer> zUsedNumbers, Set<Integer> usedLabelsNumbers, long instructionNumber, Map<String, String> oldAndNew, Instruction parent){
        Instruction newInstruction;
        Variable variable, newVariableImpl;
        Label label, newLabelImpl;

        //check label
        label = this.getLabel();
        newLabelImpl = ExpansionUtils.validateOrCreateLabel(label, usedLabelsNumbers, oldAndNew);

        //check variable
        variable = this.getVariable();
        newVariableImpl = ExpansionUtils.validateOrCreateVariable(variable, zUsedNumbers, oldAndNew);

        newInstruction = new ConstantAssignmentInstruction(newVariableImpl, newLabelImpl, this.constantValue, instructionNumber, parent);

        return newInstruction;
    }

    @Override
    public Instruction cloneWithDifferentNumber(long number){
        Instruction newInstruction;
        newInstruction = new ConstantAssignmentInstruction(this.getVariable(),this.getLabel(), this.constantValue, number, this.getParent());
        return newInstruction;
    }
}
