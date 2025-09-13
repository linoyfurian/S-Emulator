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

import java.util.*;

public class QuoteInstruction extends AbstractInstruction implements ComplexInstruction {
    private final String functionName;
    private final String functionArguments;


    public QuoteInstruction(Variable variable, String functionName, String functionArguments, long instructionNumber, Instruction parent) {
        this(variable, functionName, functionArguments, FixedLabel.EMPTY, instructionNumber, parent);

    }

    public QuoteInstruction(Variable variable, String functionName, String functionArguments, Label label, long instructionNumber, Instruction parent) {
        super(InstructionData.QUOTE, variable, label, InstructionType.SYNTHETIC, 0, instructionNumber, parent);
        this.functionName = functionName;
        this.functionArguments = functionArguments;
    }

    @Override
    public Label execute(ExecutionContext context, Program program) {
        ProgramImpl programImpl = (ProgramImpl) program;
        List<Program> functions = programImpl.getFunctions();
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

            ExecutionRunDto runDetails = programExecutor.run(0, 0, null, inputs);
            if(runDetails!=null) {
                assignedValue = runDetails.getResult();
                context.updateVariable(this.getVariable(), assignedValue);
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
                    if(functionName.equalsIgnoreCase(f.getName())) {
                        functionUserName = f.getUserString();
                        break;
                    }
                }
            }

            if (functionArguments.equals(""))
                return (getVariable().getRepresentation() + " <- " + "(" + functionUserName +  ")");
            else{
                String useStringFunctionArguments = FunctionUtils.generateUserStringFunctionArguments(this.functionArguments, functions);
                return (getVariable().getRepresentation() + " <- " + "(" + functionUserName + "," + useStringFunctionArguments + ")");
            }
       }

        return "";
    }



    @Override
    public List<Instruction> expand(Set<Integer> zUsedNumbers, Set<Integer> usedLabelsNumbers, long instructionNumber, Program program) {
        List<Instruction> nextInstructions = new ArrayList<>();
        ProgramImpl programImpl = (ProgramImpl) program;

        List<Program> functions = programImpl.getFunctions();
        Map<String, String> oldAndNew = new HashMap<>();

        Function functionToRun = null;
        Instruction newInstruction;

        functionToRun = ExpansionUtils.findFunctionInProgram(functions, functionName);

        //add first instruction
        if(this.getLabel()!=FixedLabel.EMPTY) {
            newInstruction = new NeutralInstruction(Variable.RESULT, this.getLabel(), instructionNumber,this);
            nextInstructions.add(newInstruction);
            instructionNumber++;
        }

        List<Instruction> newQ = generatenewQInstructions(functionToRun, zUsedNumbers, usedLabelsNumbers, oldAndNew); //Q'

        String inputArgument;
        List<String> arguments = FunctionUtils.splitFunctionArguments(this.functionArguments);


        for(int i = 0; i < arguments.size(); i++) {
            String currArgument = arguments.get(i).trim();
            inputArgument = "x" + (i+1);
            if(oldAndNew.containsKey(inputArgument)) {
                Variable newVariable, newAssignedVariable;
                newVariable = XmlProgramMapperV2.variableMapper(oldAndNew.get(inputArgument));
                newAssignedVariable = XmlProgramMapperV2.variableMapper(currArgument);

                if(FunctionUtils.isVariableIsAFunction(currArgument)) {
                    String newFunctionName = XmlProgramMapperV2.getFunctionName(currArgument);
                    String newFunctionArguments = XmlProgramMapperV2.getFunctionarguments(currArgument);
                    newInstruction = new QuoteInstruction(newVariable, newFunctionName, newFunctionArguments, instructionNumber, this);

                    QuoteInstruction ci=(QuoteInstruction) newInstruction;
                }
                else
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
