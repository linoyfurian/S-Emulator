package semulator.logic.instruction;

import semulator.logic.execution.ExecutionContext;
import semulator.logic.label.Label;

import java.util.Map;
import java.util.Set;

public interface SimpleInstruction {
    Label execute(ExecutionContext context);
    String getInstructionDescription();
    Instruction QuoteFunctionExpandHelper(Set<Integer> zUsedNumbers, Set<Integer> usedLabelsNumbers, long instructionNumber, Map<String, String> oldAndNew, Instruction parent);
    Instruction cloneWithDifferentNumber(long number);
}
