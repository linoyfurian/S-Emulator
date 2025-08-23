package semulator.logic.program;

import semulator.logic.instruction.Instruction;
import semulator.logic.label.Label;
import semulator.logic.variable.Variable;

import java.util.LinkedHashSet;
import java.util.List;

public interface Program {

    String getName();
    void addInstruction(Instruction instruction);
    List<Instruction> getInstructions();
    boolean validate();
    int calculateMaxDegree();
    int calculateCycles();
    LinkedHashSet<Variable> getVariables();
    LinkedHashSet<Label> getLabels();
    Program expand(int degreeOfExpand);
    void addVariable(Variable variable);
}
