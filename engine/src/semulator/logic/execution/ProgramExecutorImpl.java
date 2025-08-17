package semulator.logic.execution;

import semulator.logic.instruction.ExpandableInstruction;
import semulator.logic.instruction.Instruction;
import semulator.logic.instruction.InstructionType;
import semulator.logic.instruction.expansion.ExpansionUtils;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.program.Program;
import semulator.logic.program.ProgramImpl;
import semulator.logic.variable.Variable;
import semulator.logic.variable.VariableType;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProgramExecutorImpl implements ProgramExecutor{

    private final Program programToRun;

    public ProgramExecutorImpl(Program programToRun) {
        this.programToRun = programToRun;
    }

    @Override
    public ExecutionContext run(Long... inputs) {
//TODO :
        ExecutionContext context = new ExecutionContextImpl();

        //TODO ask if user can insert less arguments
        int inputIndex = 0;
        for (Variable v : programToRun.getVariables()) {
            if (v.getType() == VariableType.INPUT) {
                long value = (inputIndex < inputs.length) ? inputs[inputIndex] : 0L;
                context.updateVariable(v, value);
                inputIndex++;
            }
            else
                context.updateVariable(v, 0L);
        }
        long pc = 0;
        List<Instruction> instructions = programToRun.getInstructions();
        Instruction currentInstruction = instructions.get((int) pc); //first instruction
        Label nextLabel;
        do {
            nextLabel = currentInstruction.execute(context);

            if (nextLabel == FixedLabel.EMPTY) {//next instruction
                pc++;
            } else if (nextLabel != FixedLabel.EXIT) {
                pc = 0;
                for (Instruction instruction : instructions) {
                    if (instruction.getLabel().getLabelRepresentation().equals(nextLabel.getLabelRepresentation())) {
                        break;
                    } else
                        pc++;
                }
            }
            currentInstruction = instructions.get((int) pc);
        } while (nextLabel != FixedLabel.EXIT);

        return context;
    }

    @Override
    public Map<Variable, Long> variableState() {
        return Map.of();
    }

    public static Program expand(int degree, Program program){
        Program expandedProgram = program;
        if(degree==0)
            return expandedProgram;

        long instructionNumber;

        while(degree>0){
            Program nextExpandedProgram = new ProgramImpl(program.getName());
            Set<Integer> zUsedNumbers, usedLabelsNumbers;

            zUsedNumbers = ExpansionUtils.getSetOfUsedZNumbers(program.getVariables());
            usedLabelsNumbers = ExpansionUtils.getSetOfUsedLabels(program.getLabels());

            instructionNumber = 1;

            for(Instruction instruction : program.getInstructions()) {
                if (instruction.getType() == InstructionType.BASIC) {
                    nextExpandedProgram.addInstruction(instruction);
                } else if (instruction instanceof ExpandableInstruction expandableInstruction) {
                    List<Instruction> nextInstructions = expandableInstruction.expand(zUsedNumbers, usedLabelsNumbers, instructionNumber);

                    for (Instruction nextInstruction : nextInstructions) {
                        nextExpandedProgram.addInstruction(nextInstruction);
                    }
                    instructionNumber = instructionNumber + nextInstructions.size();
                }
            }

            degree--;
        }
        return expandedProgram;
    }
}