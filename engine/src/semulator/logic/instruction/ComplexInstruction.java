package semulator.logic.instruction;

import semulator.logic.Function.FunctionUtils;
import semulator.logic.execution.ExecutionContext;
import semulator.logic.label.Label;
import semulator.logic.program.Program;
import semulator.logic.program.ProgramImpl;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ComplexInstruction {
    Label execute(ExecutionContext context, Program program);
    List<Instruction> expand(Set<Integer> zUsedNumbers, Set<Integer> usedLabelsNumbers, long instructionNumber, Map<String, String> oldAndNew, Program program);
    String getInstructionDescription(Program program);
    boolean isComposite();
    void updateDegreeOfExpansion(Program program);
    Instruction QuoteFunctionExpandHelper(Set<Integer> zUsedNumbers, Set<Integer> usedLabelsNumbers, long instructionNumber, Map<String, String> oldAndNew, Instruction parent);
    Instruction cloneWithDifferentNumber(long instructionNumber);
    int findDepthOfFunction();
}
