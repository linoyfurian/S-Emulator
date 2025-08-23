package semulator.logic.program;

import semulator.logic.instruction.Instruction;
import semulator.logic.instruction.InstructionDto;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.label.LabelImpl;
import semulator.logic.variable.Variable;
import semulator.logic.variable.VariableType;

import java.util.*;

public class ProgramDto {
    private final String programName;
    private final List<InstructionDto> instructions;
    private final List<String> inputVariablesInOrder;
    private final List<String> labelsInOrder;


    public ProgramDto(Program program) {
        this.programName = program.getName();

        this.inputVariablesInOrder =  new ArrayList<>();
        this.labelsInOrder = new ArrayList<>();

        List<Integer> labelsNumbers = new ArrayList<>();
        List<Integer> variablesNumbers = new ArrayList<>();

        LinkedHashSet<Variable> allVars = program.getVariables();
        LinkedHashSet<Label> labels = program.getLabels();

        boolean sawExit = false;

        for(Label label : labels) {
            if (label instanceof LabelImpl labelImpl)
                labelsNumbers.add(labelImpl.getNumber());
            if (label == FixedLabel.EXIT)
                sawExit = true;
        }

        Collections.sort(labelsNumbers);
        for(Integer number : labelsNumbers) {
            labelsInOrder.add(("L" + number));
        }

        if (sawExit)
            labelsInOrder.add(FixedLabel.EXIT.getLabelRepresentation());


        for(Variable variable : allVars) {
            if(variable!=null){
                if (variable.getType() == VariableType.INPUT) {
                    variablesNumbers.add(variable.getNumber());
                }
            }
        }

        Collections.sort(variablesNumbers);
        for(Integer number : variablesNumbers) {
            inputVariablesInOrder.add(("x" + number));
        }

        List<Instruction> programInstructions = program.getInstructions();
        this.instructions = new ArrayList<>();

        for (Instruction instruction : programInstructions) {
            this.instructions.add(new InstructionDto(instruction));
        }
    }

    public String getProgramName() {
        return programName;
    }

    public List<String> getInputVariablesInOrder() {
        return inputVariablesInOrder;
    }

    public List<String> getLabelsInOrder() {
        return labelsInOrder;
    }

    public List<InstructionDto> getInstructions() {
        return instructions;
    }
}

