package semulator.logic.execution;

import semulator.logic.instruction.Instruction;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.program.Program;
import semulator.logic.program.ProgramDto;
import semulator.logic.variable.Variable;
import semulator.logic.variable.VariableType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProgramExecutorImpl implements ProgramExecutor{

    private final Program programToRun;

    public ProgramExecutorImpl(Program programToRun) {
        this.programToRun = programToRun;
    }

    @Override
    public ExecutionRunDto run(int degreeOfExpansion, long runNumber, long... inputs) {

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
        int pc = 0;
        List<Instruction> instructions = programToRun.getInstructions();
        Instruction currentInstruction = instructions.get(pc); //first instruction
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
            currentInstruction = instructions.get(pc);
        } while ((nextLabel != FixedLabel.EXIT)||pc!=instructions.size());

        long y = context.getVariableValue(Variable.RESULT);
        Map<String, Long> variablesValues = context.getAllValues();
        ProgramDto programInfo = new ProgramDto(programToRun);

        ExecutionRunDto result = new ExecutionRunDto(runNumber, degreeOfExpansion, y, inputs, programToRun.calculateCycles(), variablesValues, programInfo);
        return result;
    }

    @Override
    public Map<Variable, Long> variableState() {
        return Map.of();
    }

}