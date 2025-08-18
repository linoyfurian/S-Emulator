package semulator.logic.instruction;

import semulator.logic.execution.ExecutionContext;
import semulator.logic.label.Label;
import semulator.logic.variable.Variable;

import java.util.List;

public interface Instruction {
    String getName();
    Label execute(ExecutionContext context);
    int cycles();
    Label getLabel();
    Variable getVariable();
    int getExpansionDegree();
    long getInstructionNumber();
    InstructionType getType();
    String getInstructionDescription();
    List<Variable> getAllVariables();
    List<Label> getAllLabels();
}


