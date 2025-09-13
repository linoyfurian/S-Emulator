package semulator.logic.debugger;

import semulator.api.dto.DebugContextDto;
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
    private final Program mainProgram;
    private int cycles;

    public ProgramDebuggerImpl(Program programToDebug, Program mainProgram, long ... input) {
        this.programToDebug = programToDebug;
        this.mainProgram = mainProgram;
        this.context = new ExecutionContextImpl();

        //add input variables
        for (int i=0; i<input.length; i++) {
            String variableName = "x"+(i+1);
            Variable variable = new VariableImpl(VariableType.INPUT,i+1, variableName);
            context.updateVariable(variable, input[i]);
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

    public ProgramDebuggerImpl(Program programToDebug, DebugContextDto debugContext, Program mainProgram) {
        this.programToDebug = programToDebug;
        this.mainProgram = mainProgram;
        Map<String,Long> variablesValues = debugContext.getCurrentVariablesValues();
        this.context = new ExecutionContextImpl(variablesValues);
        this.cycles = debugContext.getCycles();
    }

    @Override
    public DebugContextDto debug (long instructionToExecuteNumber, DebugContextDto debugDetails){

        List<Instruction> instructions = programToDebug.getInstructions();
        //todo check size of instructions
        Instruction instructionToExecute = instructions.get((int)(instructionToExecuteNumber - 1));
        Label nextLabel;
        int cycles;

        Map<String,Long> previousVariablesValues = this.context.getAllValues();

        if(instructionToExecute instanceof SimpleInstruction simpleInstruction)
            nextLabel = simpleInstruction.execute(this.context);
        else {
            ComplexInstruction complexInstruction = (ComplexInstruction) instructionToExecute;
            nextLabel = complexInstruction.execute(this.context, mainProgram);
        }

        cycles = this.cycles + instructionToExecute.cycles();

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

        DebugContextDto result = new DebugContextDto(programToDebug, this.context, instructionToExecuteNumber, nextInstructionNumber, cycles, previousVariablesValues, debugDetails);

        return result;
    }
}
