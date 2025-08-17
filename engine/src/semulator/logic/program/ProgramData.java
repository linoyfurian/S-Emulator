package semulator.logic.program;

import semulator.logic.instruction.AssignmentInstruction;
import semulator.logic.instruction.Instruction;
import semulator.logic.instruction.JumpEqualVariableInstruction;
import semulator.logic.label.FixedLabel;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class ProgramData {
    private final String programName;
    private final List<Instruction> instructions;
    private final List<String> variablesInOrder;
    private final List<String> labelsInOrder;

    public ProgramData(Program program) {
        this.programName = program.getName();
        //this.instructions = program.getInstructions();

        LinkedHashSet<String> allVars = new LinkedHashSet<>();
        LinkedHashSet<String> labels = new LinkedHashSet<>();

        boolean sawExit = false;

        List<Instruction> instructions = program.getInstructions();
        this.instructions = instructions;

        for (Instruction instruction : instructions) {
            if ((instruction.getLabel() != FixedLabel.EMPTY) && (instruction.getLabel() != FixedLabel.EXIT))
                labels.add(instruction.getLabel().getLabelRepresentation());
            if (instruction.getLabel() == FixedLabel.EXIT)
                sawExit = true;
        }

        List<String> labelsOrdered = new ArrayList<>(labels);

        if (sawExit)
            labelsOrdered.add(FixedLabel.EXIT.getLabelRepresentation());

        this.labelsInOrder = labelsOrdered;


        for (Instruction instruction : instructions) {
            allVars.add(instruction.getVariable().getRepresentation());
            //TODO INTERFACE MORE THAN 1 VARIABLE
            if (instruction instanceof AssignmentInstruction ai) {
                allVars.add(ai.getAssignedVariable().getRepresentation());
            } else if (instruction instanceof JumpEqualVariableInstruction jevI) {
                allVars.add(jevI.getVariableName().getRepresentation());
            }
        }

        List<String> allVarsOrdered = new ArrayList<>(allVars);
        this.variablesInOrder = allVarsOrdered;
    }
}
