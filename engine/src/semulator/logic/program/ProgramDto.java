package semulator.logic.program;

import semulator.logic.instruction.Instruction;
import semulator.logic.instruction.InstructionDto;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.variable.Variable;
import semulator.logic.variable.VariableType;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class ProgramDto {
    private final String programName;
    private final List<InstructionDto> instructions;
    private final List<String> inputVariablesInOrder;
    private final List<String> labelsInOrder;


    public ProgramDto(Program program) {
        this.programName = program.getName();

        this.inputVariablesInOrder =  new ArrayList<>();
        this.labelsInOrder = new ArrayList<>();

        LinkedHashSet<Variable> allVars = program.getVariables();
        LinkedHashSet<Label> labels = program.getLabels();

        boolean sawExit = false;

        for(Label label : labels) {
            if ((label != FixedLabel.EMPTY) && (label != FixedLabel.EXIT))
                labelsInOrder.add(label.getLabelRepresentation());
            if (label == FixedLabel.EXIT)
                sawExit = true;
        }

        if (sawExit)
            labelsInOrder.add(FixedLabel.EXIT.getLabelRepresentation());


        for(Variable variable : allVars) {
            if (variable.getType() == VariableType.INPUT) {
                inputVariablesInOrder.add(variable.getRepresentation());
            }
        }

        List<Instruction> programInstructions = program.getInstructions();
        this.instructions = new ArrayList<>();

        for (Instruction instruction : programInstructions) {
          this.instructions.add(new InstructionDto(instruction));
        }
    }
}

