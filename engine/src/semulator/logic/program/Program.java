package semulator.logic.program;

import semulator.logic.instruction.Instruction;
import semulator.logic.label.Label;
import semulator.logic.variable.Variable;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Function;

public interface Program {

    String getName();
    void addInstruction(Instruction instruction);
    List<Instruction> getInstructions();
    boolean validate();
    int calculateMaxDegree();
    LinkedHashSet<Variable> getVariables();
    LinkedHashSet<Label> getLabels();
    int getDegree();
    int findMaxDepth();
    String getUsername();
}
