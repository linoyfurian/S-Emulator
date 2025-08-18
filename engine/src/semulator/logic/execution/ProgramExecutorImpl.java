package semulator.logic.execution;

import semulator.logic.instruction.Instruction;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.program.Program;
import semulator.logic.variable.Variable;
import semulator.logic.variable.VariableType;

import java.util.List;
import java.util.Map;

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

}