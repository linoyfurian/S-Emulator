package semulator.logic.debugger;

import dto.DebugContextDto;
import dto.ExecutionRunDto;
import semulator.logic.execution.ComplexExecuteResult;
import semulator.logic.execution.ExecutionContext;
import semulator.logic.execution.ExecutionContextImpl;
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

public class ProgramDebuggerImpl implements ProgramDebugger {
    private final Program programToDebug;
    private final ExecutionContext context;
    private final Map<String, Program> functions;
    private int cycles;
    private final String userName;

    public ProgramDebuggerImpl(String userName, Program programToDebug, Map<String, Program> functions, Map<String, Long> originalInputs, long ... input) {
        this.programToDebug = programToDebug;
        this.functions = functions;
        this.context = new ExecutionContextImpl();
        this.userName = userName;

        //add input variables
        for (int i=0; i<input.length; i++) {
            String variableName = "x"+(i+1);
            if(originalInputs.containsKey(variableName)){
                Variable variable = new VariableImpl(VariableType.INPUT,i+1, variableName);
                context.updateVariable(variable, input[i]);
            }
        }

        for (Variable variable : programToDebug.getVariables()) {
            if(variable != null){
                if (variable.getType() == VariableType.INPUT) {
                    if(variable.getNumber()>input.length) {
                        context.updateVariable(variable, 0L);
                    }
                } else
                    context.updateVariable(variable, 0L);
            }
        }

        this.cycles = 0;
    }

    public ProgramDebuggerImpl(String userName, Program programToDebug, DebugContextDto debugContext, Map<String, Program> functions) {
        this.programToDebug = programToDebug;
        this.functions = functions;
        Map<String,Long> variablesValues = debugContext.getCurrentVariablesValues();
        this.context = new ExecutionContextImpl(variablesValues);
        this.cycles = debugContext.getCycles();
        this.userName = userName;
    }

    @Override
    public DebugContextDto resume (int credits, long instructionToExecuteNumber, DebugContextDto debugDetails, Map<String, Long> originalInputs){
        ComplexExecuteResult executeResult;
        List<Instruction> instructions = programToDebug.getInstructions();

        Label nextLabel;
        int cycles = this.cycles;
        int prevCycles = this.cycles;
        int creditsCost = 0;
        int currentCycles=0;
        Map<String,Long> previousVariablesValues = this.context.getAllValues();

        int currInstructionNumber = (int)(instructionToExecuteNumber);
        while (currInstructionNumber != 0 && currInstructionNumber <= instructions.size()) {
            previousVariablesValues = this.context.getAllValues();
            Instruction instruction = instructions.get(currInstructionNumber-1);

            if(instruction instanceof SimpleInstruction simpleInstruction)
                nextLabel = simpleInstruction.execute(this.context);
            else {
                ComplexInstruction complexInstruction = (ComplexInstruction) instruction;
                executeResult = complexInstruction.execute(this.context, functions);
                nextLabel = executeResult.getNextLabel();
                creditsCost = creditsCost + executeResult.getRunCycles();
                cycles = cycles + executeResult.getRunCycles();
            }

            creditsCost = creditsCost + instruction.cycles();

            if(credits<creditsCost){
                //NOT ENOUGH CREDITS
                return new DebugContextDto(false, currentCycles, userName, programToDebug, previousVariablesValues, currInstructionNumber, 0, cycles, previousVariablesValues, debugDetails, originalInputs, prevCycles);
            }
            currentCycles = creditsCost;
            cycles = cycles + instruction.cycles();

            long nextInstructionNumber, nextInstructionIndex = 0;

            if (nextLabel == FixedLabel.EMPTY) {//next instruction
                nextInstructionNumber = currInstructionNumber + 1;
            } else if (nextLabel != FixedLabel.EXIT) {
                for (Instruction inst : instructions) {
                    if (inst.getLabel().getLabelRepresentation().equals(nextLabel.getLabelRepresentation())) {
                        break;
                    } else
                        nextInstructionIndex++;
                }
                nextInstructionNumber = nextInstructionIndex + 1;
            }
            else
                nextInstructionNumber = 0;


            if (nextInstructionNumber <= 0 || nextInstructionNumber > instructions.size()) { //exit
                nextInstructionNumber = 0;
            }
            currInstructionNumber = (int)nextInstructionNumber;
        }

        Map<String,Long> currentVariablesValues = this.context.getAllValues();

        DebugContextDto result = new DebugContextDto(true, currentCycles, userName, programToDebug, this.context, instructionToExecuteNumber, 0, cycles, previousVariablesValues, debugDetails, originalInputs, prevCycles);
        return result;
    }

    @Override
    public DebugContextDto debug (int credits, long instructionToExecuteNumber, DebugContextDto debugDetails, Map<String, Long> originalInputs){
        ComplexExecuteResult executeResult;
        List<Instruction> instructions = programToDebug.getInstructions();

        Instruction instructionToExecute = instructions.get((int)(instructionToExecuteNumber - 1));
        Label nextLabel;
        int cycles=0;
        int currentInstructionCycles;
        int prevCycles = this.cycles;
        Map<String,Long> previousVariablesValues = this.context.getAllValues();

        if(instructionToExecute instanceof SimpleInstruction simpleInstruction)
            nextLabel = simpleInstruction.execute(this.context);
        else {
            ComplexInstruction complexInstruction = (ComplexInstruction) instructionToExecute;
            executeResult = complexInstruction.execute(this.context, functions);
            nextLabel = executeResult.getNextLabel();
            cycles = cycles + executeResult.getRunCycles();
        }

        if(cycles+instructionToExecute.cycles()>credits){

            return new DebugContextDto(false, debugDetails);
        }

        currentInstructionCycles = cycles + instructionToExecute.cycles();
        cycles = cycles + this.cycles + instructionToExecute.cycles();

        long nextInstructionNumber = 0, nextInstructionIndex = 0;

        if (nextLabel == FixedLabel.EMPTY) {//next instruction
            nextInstructionNumber = instructionToExecuteNumber + 1;
        } else if (nextLabel != FixedLabel.EXIT) {
            for (Instruction instruction : instructions) {
                if (instruction.getLabel().getLabelRepresentation().equals(nextLabel.getLabelRepresentation())) {
                    break;
                } else
                    nextInstructionIndex++;
            }
            nextInstructionNumber = nextInstructionIndex + 1;
        }
        else
            nextInstructionNumber = 0;

        if (nextInstructionNumber <= 0 || nextInstructionNumber > instructions.size()) { //exit
            nextInstructionNumber = 0;
        }


        Map<String,Long> currentVariablesValues = this.context.getAllValues();

        DebugContextDto result = new DebugContextDto(true, currentInstructionCycles, userName, programToDebug, this.context, instructionToExecuteNumber, nextInstructionNumber, cycles, previousVariablesValues, debugDetails, originalInputs, prevCycles);
        return result;
    }

    @Override
    public DebugContextDto initialDebugger(Map<String, Long> originalInputs){
        DebugContextDto result;
        int instructionToExecuteNumber = 0;
        int nextInstructionNumber = 1;
        int prevCycles = this.cycles; //todo delete
        Map<String,Long> previousVariablesValues = this.context.getAllValues();
        result = new DebugContextDto(true, 0, userName, programToDebug, this.context, instructionToExecuteNumber, nextInstructionNumber, cycles, previousVariablesValues, null, originalInputs, prevCycles);
        return result;
    }

    @Override
    public DebugContextDto breakPointMode(long breakPointIndex, DebugContextDto debugDetails, Map<String, Long> originalInputs) {
        DebugContextDto result;
        int instructionToExecuteNumber = 1;
        List<Instruction> instructions = programToDebug.getInstructions();
        Label nextLabel;
        int cycles = 0;
        ComplexExecuteResult executeResult;

        Map<String,Long> previousVariablesValues = this.context.getAllValues();

        while(instructionToExecuteNumber != breakPointIndex && instructionToExecuteNumber!=0){
            Instruction instruction = instructions.get(instructionToExecuteNumber-1);

            if(instruction instanceof SimpleInstruction simpleInstruction)
                nextLabel = simpleInstruction.execute(this.context);
            else {
                ComplexInstruction complexInstruction = (ComplexInstruction) instruction;
                executeResult = complexInstruction.execute(this.context, functions);
                nextLabel = executeResult.getNextLabel();
                cycles = cycles + executeResult.getRunCycles();
            }

            cycles = cycles + instruction.cycles();

            long nextInstructionNumber, nextInstructionIndex = 0;

            if (nextLabel == FixedLabel.EMPTY) {//next instruction
                nextInstructionNumber = instructionToExecuteNumber + 1;
            } else if (nextLabel != FixedLabel.EXIT) {
                for (Instruction inst : instructions) {
                    if (inst.getLabel().getLabelRepresentation().equals(nextLabel.getLabelRepresentation())) {
                        break;
                    } else
                        nextInstructionIndex++;
                }
                nextInstructionNumber = nextInstructionIndex + 1;
            }
            else
                nextInstructionNumber = 0;


            if (nextInstructionNumber <= 0 || nextInstructionNumber > instructions.size()) { //exit
                nextInstructionNumber = 0;
            }
            instructionToExecuteNumber = (int) nextInstructionNumber;
        }

        //todo fix 0 cycles true
        result = new DebugContextDto(true,0, userName, programToDebug, this.context, instructionToExecuteNumber, instructionToExecuteNumber, cycles, previousVariablesValues, debugDetails, originalInputs, 0);
        return result;
    }
}
