package semulator.logic.instruction;

import semulator.logic.execution.ComplexExecuteResult;
import semulator.logic.execution.ExecutionContext;
import semulator.logic.program.Program;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ComplexInstruction {
    ComplexExecuteResult execute(ExecutionContext context, Map<String, Program> functions);
    List<Instruction> expand(Set<Integer> zUsedNumbers, Set<Integer> usedLabelsNumbers, long instructionNumber, Map<String, String> oldAndNew, Map<String, Program> functions);
    String getInstructionDescription(Map<String, Program> functions);
    boolean isComposite();
    void updateDegreeOfExpansion(Program program);
    Instruction QuoteFunctionExpandHelper(Set<Integer> zUsedNumbers, Set<Integer> usedLabelsNumbers, long instructionNumber, Map<String, String> oldAndNew, Instruction parent);
    Instruction cloneWithDifferentNumber(long instructionNumber);
    int findDepthOfFunction();
    String getNameOfFunction();
}
