package semulator.logic.execution;

import semulator.logic.instruction.ExpandableInstruction;
import semulator.logic.instruction.Instruction;
import semulator.logic.instruction.InstructionType;
import semulator.logic.program.Program;
import semulator.logic.program.ProgramImpl;
import semulator.logic.variable.Variable;

import java.util.List;
import java.util.Map;

public class ProgramExecutorImpl implements ProgramExecutor{

    private final Program programToRun;

    public ProgramExecutorImpl(Program programToRun) {
        this.programToRun = programToRun;
    }

    @Override
    public ExecutionContext run(Long... input) {
//TODO :
        ExecutionContext context = new ExecutionContextImpl();

        int inputIndex = 0;
        for (Variable v : programToRun.getVariables()) {
            if (v.getRepresentation().startsWith("x")) {
            //    long value = (inputIndex < userInputs.size()) ? userInputs.get(inputIndex) : 0L;
                context.updateVariable(v, value);
                inputIndex++;
            return context;



//
//        Instruction currentInstruction = program.getInstructions().get(0);
//        Label nextLabel;
//        do {
//            nextLabel = currentInstruction.execute(context);
//
//            if (nextLabel == FixedLabel.EMPTY) {
//                // set currentInstruction to the next instruction in line
//            } else if (nextLabel != FixedLabel.EXIT) {
//                // need to find the instruction at 'nextLabel' and set current instruction to it
//            }
//        } while (nextLabel != FixedLabel.EXIT);
//
//        //return context.getVariableValue(Variable.RESULT);
//        return context;
    }

    @Override
    public Map<Variable, Long> variableState() {
        return Map.of();
    }

    @Override
    public Program expand(int degree){
        Program expandedProgram = this.program;
        if(degree==0)
            return expandedProgram;

        while(degree>0){

            Program nextExpandedProgram = new ProgramImpl(this.program.getName());
            for(Instruction instruction : this.program.getInstructions()) {
                if (instruction.getType() == InstructionType.BASIC) {
                    nextExpandedProgram.addInstruction(instruction);
                } else if (instruction instanceof ExpandableInstruction expandableInstruction) {
                    List<Instruction> nextInstructions = expandableInstruction.expand(); //TODO : implement!!

                    for (Instruction nextInstruction : nextInstructions) {
                        nextExpandedProgram.addInstruction(nextInstruction);
                    }
                }
            }

            degree--;
        }
        return expandedProgram;
    }

    @Override
    public void setProgramToRun(Program programToRun) {
        this.programToRun = programToRun;
    }
}