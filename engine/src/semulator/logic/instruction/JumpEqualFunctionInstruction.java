package semulator.logic.instruction;

import semulator.api.dto.ExecutionRunDto;
import semulator.core.loader.XmlProgramMapperV2;
import semulator.logic.Function.Function;
import semulator.logic.Function.FunctionUtils;
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

            ExecutionRunDto runDetails = programExecutor.run(0, 0, null, inputs);
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
                if (function instanceof Function f) {
                    if (functionName.equals(f.getName())) {
                        functionUserName = f.getUserString();
                        break;
                    }
                }
            }
            if (functionArguments.equals("") || functionArguments == null)
                return ("IF" + getVariable().getRepresentation() + " = " + "(" + functionUserName + ") GOTO " + JEFunctionLabel.getLabelRepresentation());
            else {
                String useStringFunctionArguments = FunctionUtils.generateUserStringFunctionArguments(this.functionArguments, functions);
                return ("IF" + getVariable().getRepresentation() + " = " + "(" + functionUserName + "," + useStringFunctionArguments + ") GOTO " + JEFunctionLabel.getLabelRepresentation());
            }
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

    @Override
    public boolean isComposite(){
        boolean result = false;
        List<String> functionArguments = FunctionUtils.splitFunctionArguments(this.functionArguments);
        if(functionArguments.size()>0){
            for(String functionArgument : functionArguments){
                if(functionArgument.startsWith("("))
                    result = true;
                break;
            }
        }
        return result;
    }

    @Override
    public List<Label> getAllLabels(){
        List<Label> allLabels = new ArrayList<>();
        allLabels.add(this.getLabel());
        allLabels.add(JEFunctionLabel);
        return allLabels;
    }


    @Override
    public List<Variable> getAllVariables(){
        Set <String> allVariables = new HashSet<>();
        allVariables.add(this.getVariable().getRepresentation());
        boolean isFunction = false, isFunctionName = false, isArg = true;
        String currFunctionArgument="";
        List<Variable> variablesResult = new ArrayList<>();

        if(this.functionArguments.isEmpty()||this.functionArguments.equals("")){
            variablesResult.add(this.getVariable());
            return variablesResult;
        }

        for (int i = 0; i < this.functionArguments.length(); i++) {
            char c = this.functionArguments.charAt(i);
            if(c=='('){
                isFunction = true;
                isFunctionName = true;
                isArg = false;
            }
            else if(c==','){
                if(isFunction){
                    if(isFunctionName){
                        isFunctionName = false;
                        isArg = true;
                    }
                    else {
                        allVariables.add(currFunctionArgument);
                        currFunctionArgument ="";
                    }
                }
                else {
                    allVariables.add(currFunctionArgument);
                    currFunctionArgument = "";
                }
            }
            else if(c==')'){
                isFunction = false;
                isFunctionName = false;
                isArg = true;
            }
            else if(isArg)
                currFunctionArgument = currFunctionArgument + c;
        }

        if(!currFunctionArgument.equals(""))
            allVariables.add(currFunctionArgument);

        for(String variable : allVariables){
            if(!variable.equals(""))
                variablesResult.add(XmlProgramMapperV2.variableMapper(variable));
        }

        return variablesResult;
    }
}
