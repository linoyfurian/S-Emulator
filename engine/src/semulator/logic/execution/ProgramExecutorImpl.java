package semulator.logic.execution;

import semulator.api.dto.ExecutionRunDto;
import semulator.logic.instruction.Instruction;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.program.Program;
import semulator.logic.variable.Variable;
import semulator.logic.variable.VariableImpl;
import semulator.logic.variable.VariableType;

import java.util.List;
import java.util.Map;

public class ProgramExecutorImpl implements ProgramExecutor {

    private final Program programToRun;

    public ProgramExecutorImpl(Program programToRun) {
        this.programToRun = programToRun;
    }

    @Override
    public ExecutionRunDto run(int degreeOfExpansion, long runNumber, long... inputs) {
        int cycles = 0;
        ExecutionContext context = new ExecutionContextImpl();

        //add input variables
        for (int i=0; i<inputs.length; i++) {
            Variable variable = new VariableImpl(VariableType.INPUT,i+1);
            context.updateVariable(variable, inputs[i]);
        }

        for (Variable variable : programToRun.getVariables()) {
            if(variable != null){
                if (variable.getType() == VariableType.INPUT) {
                    if(variable.getNumber()>inputs.length) {
                        context.updateVariable(variable, 0L);
                    }
                } else
                    context.updateVariable(variable, 0L);
            }
        }

        int pc = 0;
        List<Instruction> instructions = programToRun.getInstructions();
        Instruction currentInstruction = instructions.get(pc); //first instruction



        Label nextLabel;
        do {
            nextLabel = currentInstruction.execute(context);
            cycles = cycles + currentInstruction.cycles();

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
            if (pc < 0 || pc >= instructions.size()) {
                break; // reached end or invalid pc
            }
            currentInstruction = instructions.get(pc);
        } while ((nextLabel != FixedLabel.EXIT) && (pc != instructions.size()));

        long y = context.getVariableValue(Variable.RESULT);
        Map<String, Long> variablesValues = context.getAllValues();

        ExecutionRunDto result = new ExecutionRunDto(runNumber, degreeOfExpansion, y, inputs, cycles, variablesValues);
        return result;
    }
}