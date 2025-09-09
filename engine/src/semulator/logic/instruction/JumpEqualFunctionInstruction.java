package semulator.logic.instruction;

import semulator.api.dto.ExecutionRunDto;
import semulator.core.loader.XmlProgramMapperV2;
import semulator.logic.Function.Function;
import semulator.logic.execution.ExecutionContext;
import semulator.logic.execution.ProgramExecutor;
import semulator.logic.execution.ProgramExecutorImpl;
import semulator.logic.instruction.expansion.ExpansionUtils;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.program.Program;
import semulator.logic.program.ProgramImpl;
import semulator.logic.variable.Variable;
import semulator.logic.variable.VariableImpl;
import semulator.logic.variable.VariableType;

import java.util.*;

public class JumpEqualFunctionInstruction extends AbstractInstruction implements ComplexInstruction {
    private final Label JEFunctionLabel;
    private final String functionName;
    private final String functionArguments;

    public JumpEqualFunctionInstruction(Variable variable, String functionName, String functionArguments, Label JEFunctionLabel, long instructionNumber, Instruction parent) {
        this(variable, functionName, functionArguments, FixedLabel.EMPTY, JEFunctionLabel, instructionNumber, parent);

    }

    public JumpEqualFunctionInstruction(Variable variable, String functionName, String functionArguments, Label label, Label JEFunctionLabel, long instructionNumber, Instruction parent) {
        super(InstructionData.JUMP_EQUAL_FUNCTION, variable, label, InstructionType.SYNTHETIC, 0, instructionNumber, parent);
        this.functionName = functionName;
        this.functionArguments = functionArguments;
        this.JEFunctionLabel = JEFunctionLabel;
    }


    @Override
    public Label execute(ExecutionContext context, Program program) {
        ProgramImpl programImpl = (ProgramImpl) program;
        List<Program> functions = programImpl.getFunctions();
        Function functionToRun = null;
        long functionResult = 0L, variableValue;

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
                functionResult = runDetails.getResult();
                variableValue = context.getVariableValue(this.getVariable());
                if(variableValue==functionResult) {
                    return JEFunctionLabel;
                }
            }
        }
        return FixedLabel.EMPTY;
    }

    @Override
    public String getInstructionDescription(Program program) {
        String functionUserName = "";

        if(program instanceof ProgramImpl programImpl) {
            List<Program> functions = programImpl.getFunctions();
            for (Program function : functions) {
                if(function instanceof Function f) {
                    if(functionName.equals(f.getName())) {
                        functionUserName = f.getUserString();
                        break;
                    }
                }
            }
            return ("IF" + getVariable().getRepresentation() + " = " + functionUserName + "(" + functionArguments + ") GOTO " + JEFunctionLabel.getLabelRepresentation());
        }
        return "";
    }


    @Override
    public List<Instruction> expand(Set<Integer> zUsedNumbers, Set<Integer> usedLabelsNumbers, long instructionNumber, Program program) {
        List<Instruction> nextInstructions = new ArrayList<>();
        Instruction newInstruction;
        int availableZnumber;
        Variable availableZvariable;

        availableZnumber = ExpansionUtils.findAvailableZNumber(zUsedNumbers);
        zUsedNumbers.add(availableZnumber);
        String zVariableName = "z" + availableZnumber;
        availableZvariable = new VariableImpl(VariableType.WORK, availableZnumber, zVariableName); //z1

        newInstruction = new QuoteInstruction(availableZvariable, this.functionName, this.functionArguments, this.getLabel(),instructionNumber, this); //z1<-Q(x1..)
        nextInstructions.add(newInstruction);
        instructionNumber++;

        newInstruction = new JumpEqualVariableInstruction(this.getVariable(), this.JEFunctionLabel, availableZvariable, instructionNumber, this); // IF V = Q(X1..) GOTO L
        nextInstructions.add(newInstruction);

        return nextInstructions;
    }

    public String getFunctionName() {
        return functionName;
    }

    private List<String> splitFunctionArguments() {
        List<String> arguments = new ArrayList<>();

        boolean isFunction = false;
        String currFunctionArgument="";

        for (int i = 0; i < functionArguments.length(); i++) {
            char c = functionArguments.charAt(i);
            if(c=='('){
                isFunction = true;
                currFunctionArgument = currFunctionArgument + c;
            }
            else if(c==','){
                if(isFunction)
                    currFunctionArgument = currFunctionArgument + c;
                else{
                    arguments.add(currFunctionArgument);
                    currFunctionArgument ="";
                }
            }
            else if(c==')'){
                currFunctionArgument = currFunctionArgument + c;
                isFunction = false;
            }
            else
                currFunctionArgument = currFunctionArgument + c;
        }

        if (!currFunctionArgument.equals(""))
            arguments.add(currFunctionArgument);

        return arguments;

    }

    @Override
    public boolean isComposite(){
        boolean result = false;
        List<String> functionArguments = splitFunctionArguments();
        if(functionArguments.size()>0){
            for(String functionArgument : functionArguments){
                if(functionArgument.startsWith("("))
                    result = true;
                break;
            }
        }
        return result;
    }
}
