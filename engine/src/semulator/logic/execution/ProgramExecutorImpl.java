package semulator.logic.execution;

import dto.ExecutionRunDto;
import semulator.logic.instruction.ComplexInstruction;
import semulator.logic.instruction.Instruction;
import semulator.logic.instruction.SimpleInstruction;
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
    private final Program mainProgram;

    public ProgramExecutorImpl(Program programToRun, Program mainProgram) {
        this.programToRun = programToRun;
        this.mainProgram = mainProgram;
    }

    @Override
    public ExecutionRunDto run(int degreeOfExpansion, long runNumber, Map<String, Long> originalInputs, long... inputs) {
        int cycles = 0;
        ComplexExecuteResult executeResult;
        ExecutionContext context = new ExecutionContextImpl();

        //add input variables
        for (int i=0; i<inputs.length; i++) {
            String variableName = "x"+(i+1);
            if(originalInputs!=null) {
                if(originalInputs.containsKey(variableName)){
                    Variable variable = new VariableImpl(VariableType.INPUT,i+1, variableName);
                    context.updateVariable(variable, inputs[i]);
                }
            }
            else{
                Variable variable = new VariableImpl(VariableType.INPUT,i+1, variableName);
                context.updateVariable(variable, inputs[i]);
            }
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
            if(currentInstruction instanceof SimpleInstruction simpleInstruction) {
                nextLabel = simpleInstruction.execute(context);
            }
            else{
                ComplexInstruction complexInstruction = (ComplexInstruction) currentInstruction;
                executeResult = complexInstruction.execute(context, mainProgram);
                nextLabel = executeResult.getNextLabel();
                cycles = cycles + executeResult.getRunCycles();
            }

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

        ExecutionRunDto result = new ExecutionRunDto(runNumber, degreeOfExpansion, y, cycles, variablesValues, originalInputs);
        return result;
    }
}