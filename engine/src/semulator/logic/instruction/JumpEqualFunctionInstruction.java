package semulator.logic.instruction;

import dto.ExecutionRunDto;
import semulator.core.loader.XmlProgramMapperV2;
import semulator.logic.Function.Function;
import semulator.logic.Function.FunctionUtils;
import semulator.logic.execution.*;
import semulator.logic.instruction.expansion.ExpansionUtils;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.program.Program;
import semulator.logic.program.ProgramImpl;
import semulator.logic.variable.Variable;
import semulator.logic.variable.VariableImpl;
import semulator.logic.variable.VariableType;

import java.util.*;

public class JumpEqualFunctionInstruction extends AbstractInstruction implements ComplexInstruction, JumpInstruction {
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
    public ComplexExecuteResult execute(ExecutionContext context, Map<String, Program> functions) {
        int runCycles = 0;

        Function functionToRun = null;
        long functionResult;

        for (Program function : functions.values()) {
            if(function instanceof Function f) {
                if(functionName.equals(f.getName())) {
                    functionToRun = f;
                    break;
                }
            }
        }

        if(functionToRun != null) {
            ProgramExecutor programExecutor = new ProgramExecutorImpl(functionToRun, functions);

            List<String> arguments = FunctionUtils.splitFunctionArguments(functionArguments);
            long [] inputs = new long[arguments.size()];


            ArgumentResult currArgument;
            for(int i = 0; i < arguments.size(); i++) {
                currArgument = ExecutionUtils.findInputValue(functions,arguments.get(i), context);
                inputs[i] = currArgument.getArgumentValue();
                runCycles = runCycles + currArgument.getCycles();
            }

            ExecutionRunDto runDetails = programExecutor.run(-1, 0, 0, null, inputs);

            if(runDetails!=null) {
                functionResult = runDetails.getResult();
                long variableValue = context.getVariableValue(this.getVariable());
                runCycles = runCycles + runDetails.getCycles();
                if(variableValue==functionResult) {
                    return new ComplexExecuteResult(JEFunctionLabel, runCycles);
                }
            }
        }
        return new ComplexExecuteResult(FixedLabel.EMPTY, runCycles);
    }

    @Override
    public String getInstructionDescription(Map<String, Program> functions) {
        String functionUserName = "";

        Function f = (Function) functions.get(functionName);
        if(f!=null)
            functionUserName = f.getUserString();

            if (functionArguments.equals("") || functionArguments == null)
                return ("IF " + getVariable().getRepresentation() + " = " + "(" + functionUserName + ") GOTO " + JEFunctionLabel.getLabelRepresentation());
            else {
                String useStringFunctionArguments = FunctionUtils.generateUserStringFunctionArguments(this.functionArguments, functions);
                return ("IF " + getVariable().getRepresentation() + " = " + "(" + functionUserName + "," + useStringFunctionArguments + ") GOTO " + JEFunctionLabel.getLabelRepresentation());
            }
    }


    @Override
    public List<Instruction> expand(Set<Integer> zUsedNumbers, Set<Integer> usedLabelsNumbers, long instructionNumber, Map<String, String> oldAndNew, Map<String, Program> functions) {
        List<Instruction> nextInstructions = new ArrayList<>();
        Instruction newInstruction;
        int availableZnumber;
        Variable availableZvariable;

        availableZnumber = ExpansionUtils.findAvailableZNumber(zUsedNumbers);
        zUsedNumbers.add(availableZnumber);
        String zVariableName = "z" + availableZnumber;
        availableZvariable = new VariableImpl(VariableType.WORK, availableZnumber, zVariableName); //z1

        newInstruction = new QuoteInstruction(availableZvariable, this.functionName, this.functionArguments, this.getLabel(),instructionNumber, this);
        nextInstructions.add(newInstruction);
        instructionNumber++;

        newInstruction = new JumpEqualVariableInstruction(this.getVariable(), this.JEFunctionLabel, availableZvariable, instructionNumber, this);
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


    @Override
    public void updateDegreeOfExpansion(Program program) {
        ProgramImpl programImpl = (ProgramImpl) program;
        List<String> functionArguments = FunctionUtils.splitFunctionArguments(this.functionArguments);
        List<Program> functions = programImpl.getFunctions();
        Program currentFunction = FunctionUtils.findFunction(this.functionName, functions);

        int depth = 1;
        int maxDegreeOfExpansion = Math.max(currentFunction.calculateMaxDegree() + depth, 4);
        int currentMaxDegree;
        boolean isFunctionName = false;

        String currentFunctionName = "";

        if(functionArguments.size()!=0) {
            for(int i=0; i<this.functionArguments.length(); i++){
                char c = this.functionArguments.charAt(i);
                if(c=='('){
                    depth++;
                    isFunctionName = true;
                }
                else if(c==')'){
                    if(isFunctionName){
                        isFunctionName = false;
                        Program currFunction = FunctionUtils.findFunction(currentFunctionName, functions);
                        currentMaxDegree = currFunction.calculateMaxDegree() + depth;
                        maxDegreeOfExpansion = Math.max(currentMaxDegree, maxDegreeOfExpansion);
                        maxDegreeOfExpansion = Math.max(2 + depth, maxDegreeOfExpansion);
                        currentFunctionName = "";
                    }
                    depth--;
                }
                else if(c==','){
                    if(isFunctionName){
                        isFunctionName = false;
                        Program currFunction = FunctionUtils.findFunction(currentFunctionName, functions);
                        currentMaxDegree = currFunction.calculateMaxDegree() + depth;
                        maxDegreeOfExpansion = Math.max(currentMaxDegree, maxDegreeOfExpansion);
                        maxDegreeOfExpansion = Math.max(2 + depth, maxDegreeOfExpansion);
                        currentFunctionName = "";
                    }
                }
                else{
                    if(isFunctionName)
                        currentFunctionName = currentFunctionName+c;
                }
            }
        }
        super.setMaxDegreeOfExpansion(maxDegreeOfExpansion);
    }


    @Override
    public Instruction QuoteFunctionExpandHelper(Set<Integer> zUsedNumbers, Set<Integer> usedLabelsNumbers, long instructionNumber, Map <String, String> oldAndNew, Instruction parent) {
        Instruction newInstruction;
        Label label, newLabelImpl, JEFlabel, newJEFlabelImpl;
        Variable variable, newVariableImpl;

        //check label
        label = this.getLabel();
        newLabelImpl = ExpansionUtils.validateOrCreateLabel(label, usedLabelsNumbers, oldAndNew);

        //check variable
        variable = this.getVariable();
        newVariableImpl = ExpansionUtils.validateOrCreateVariable(variable, zUsedNumbers, oldAndNew);

        //check JEF label
        JEFlabel = this.JEFunctionLabel;
        newJEFlabelImpl = ExpansionUtils.validateOrCreateLabel(JEFlabel, usedLabelsNumbers, oldAndNew);

        String newFunctionArguments = FunctionUtils.generateNewFunctionArguments(this.functionArguments, zUsedNumbers, usedLabelsNumbers, oldAndNew);

        newInstruction = new JumpEqualFunctionInstruction(newVariableImpl, this.functionName, newFunctionArguments, newLabelImpl, newJEFlabelImpl, instructionNumber, parent);

        return newInstruction;
    }

    @Override
    public Instruction cloneWithDifferentNumber(long instructionNumber){
        Instruction newInstruction;
        newInstruction = new JumpEqualFunctionInstruction(this.getVariable(), this.functionName, this.functionArguments, this.getLabel(), this.JEFunctionLabel, instructionNumber, this.getParent());
        return newInstruction;
    }

    @Override
    public int findDepthOfFunction(){
        return FunctionUtils.findDepthOfFunction(this.functionArguments);
    }

    @Override
    public Label getTargetLabel(){
        return this.JEFunctionLabel;
    }

    @Override
    public String getNameOfFunction(){
        return this.functionName;
    }
}
