package semulator.logic.instruction;

import semulator.api.dto.ExecutionRunDto;
import semulator.core.loader.XmlProgramMapperV2;
import semulator.logic.Function.Function;
import semulator.logic.execution.ExecutionContext;
import semulator.logic.execution.ProgramExecutor;
import semulator.logic.execution.ProgramExecutorImpl;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.program.Program;
import semulator.logic.program.ProgramImpl;
import semulator.logic.variable.Variable;
import semulator.logic.variable.VariableImpl;

import java.util.*;

public class QuoteInstruction extends AbstractInstruction implements ComplexInstruction {
    private final String functionName;
    private final String functionArguments;


    public QuoteInstruction(Variable variable, String functionName, String functionArguments, long instructionNumber, Instruction parent) {
        this(variable, functionName, functionArguments, FixedLabel.EMPTY, instructionNumber, parent);

    }

    public QuoteInstruction(Variable variable, String functionName, String functionArguments, Label label, long instructionNumber, Instruction parent) {
        super(InstructionData.JUMP_ZERO, variable, label, InstructionType.SYNTHETIC, 2, instructionNumber, parent);
        this.functionName = functionName;
        this.functionArguments = functionArguments;
    }

    @Override
    public Label execute(ExecutionContext context, ProgramImpl program) {
        List<Program> functions = program.getFunctions();
        Function functionToRun = null;
        long assignedValue = 0L;

        for (Program function : functions) {
            if(function instanceof Function f) {
                if(functionName.equals(f.getName())) {
                    functionToRun = f;
                    break;
                }
            }
        }
        if(functionToRun != null) {
            ProgramExecutor programExecutor = new ProgramExecutorImpl(functionToRun);

            String[] arguments = functionArguments.split(",");
            long [] inputs = new long[arguments.length];

            for(int i = 0; i < arguments.length; i++) {
                Variable var = XmlProgramMapperV2.variableMapper(arguments[i]);
                inputs[i] = context.getVariableValue(var);
            }

            ExecutionRunDto runDetails = programExecutor.run(0, 0, inputs);
            if(runDetails!=null) {
                assignedValue = runDetails.getResult();
                context.updateVariable(this.getVariable(), assignedValue);
            }
        }
        return FixedLabel.EMPTY;
    }


    @Override
    public String getInstructionDescription(ProgramImpl program) {
        String functionUserName = "";

        List<Program> functions = program.getFunctions();
        for (Program function : functions) {
            if(function instanceof Function f) {
                if(functionName.equals(f.getName())) {
                    functionUserName = f.getUserString();
                    break;
                }
            }
        }
        return (getVariable().getRepresentation() + " <- " + "(" + functionUserName + "," + functionArguments + ")");
    }



    @Override
    public List<Instruction> expand(Set<Integer> zUsedNumbers, Set<Integer> usedLabelsNumbers, long instructionNumber, ProgramImpl program) {
        List<Instruction> nextInstructions = new ArrayList<>();
        List<Program> functions = program.getFunctions();
        Map<String, String> oldAndNew = new HashMap<>();

        Function functionToRun = null;
        Instruction newInstruction;

        functionToRun = findFunctionInProgram(functions);

        //add first instruction
        if(this.getLabel()!=FixedLabel.EMPTY) {
            newInstruction = new NeutralInstruction(Variable.RESULT, this.getLabel(), instructionNumber,this);
            nextInstructions.add(newInstruction);
            instructionNumber++;
        }

        List<Instruction> newQ = generatenewQInstructions(functionToRun, zUsedNumbers, usedLabelsNumbers, oldAndNew); //Q'


        String[] arguments = functionArguments.split(",");
        for(int i = 0; i < arguments.length; i++) {
            String currArgument = arguments[i].trim();
            if(oldAndNew.containsKey(currArgument)) {
                Variable newVariable, newAssignedVariable;
                newVariable = XmlProgramMapperV2.variableMapper(oldAndNew.get(currArgument));
                newAssignedVariable = XmlProgramMapperV2.variableMapper(currArgument);
                newInstruction = new AssignmentInstruction(newVariable, instructionNumber, newAssignedVariable, this);
                nextInstructions.add(newInstruction);
                instructionNumber++;
            }
        }

        //add Q' instructions
        for(Instruction instruction : newQ) {
            if(instruction instanceof SimpleInstruction inst) {
                newInstruction = inst.cloneWithDifferentNumber(instructionNumber);
                nextInstructions.add(newInstruction);
                instructionNumber++;
            }

        }

        //add last instruction
        newInstruction = generateLastInstructionInExpand(oldAndNew, instructionNumber);
        nextInstructions.add(newInstruction);

        return nextInstructions;
    }

    private Function findFunctionInProgram(List<Program> functions) {
        Function functionToRun = null;
        for (Program function : functions) {
            if(function instanceof Function f) {
                if(functionName.equals(f.getName())) {
                    functionToRun = f;
                    break;
                }
            }
        }
        return functionToRun;
    }

    private List<Instruction> generatenewQInstructions(Function functionToRun, Set<Integer> zUsedNumbers, Set<Integer> usedLabelsNumbers, Map<String, String> oldAndNew){
        List<Instruction> originalQ = functionToRun.getInstructions(); //Q
        List<Instruction> newQ = new ArrayList<>(); //Q'

        int newQindex = 0;
        for(Instruction instruction : originalQ) {
            if(instruction instanceof SimpleInstruction inst) {
                Instruction newQInstruction = inst.QuoteFunctionExpandHelper(zUsedNumbers, usedLabelsNumbers, newQindex, oldAndNew, this);
                newQ.add(newQInstruction);
                newQindex++;
            }
        }

        return newQ;
    }

    private Instruction generateLastInstructionInExpand(Map<String, String> oldAndNew, long instructionNumber) {
        Instruction newInstruction;
        Variable newQResult;
        Label lastInstructionNewLabel;
        String lastInstructionNewLabelStr, newQResultStr;

        if(oldAndNew.containsKey(FixedLabel.EXIT.getLabelRepresentation())) {
            lastInstructionNewLabelStr = oldAndNew.get(FixedLabel.EXIT.getLabelRepresentation());
            lastInstructionNewLabel = XmlProgramMapperV2.labelMapper(lastInstructionNewLabelStr);
        }
        else
            lastInstructionNewLabel = FixedLabel.EMPTY;

        newQResultStr = oldAndNew.get(Variable.RESULT.getRepresentation());
        newQResult =  XmlProgramMapperV2.variableMapper(newQResultStr);

        newInstruction = new AssignmentInstruction(this.getVariable(), lastInstructionNewLabel, instructionNumber, newQResult, this);

        return newInstruction;
    }
}
