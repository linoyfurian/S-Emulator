package semulator.logic.program;

import semulator.core.loader.Validator;
import semulator.logic.Function.FunctionUtils;
import semulator.logic.instruction.*;
import semulator.logic.instruction.expansion.ExpansionUtils;
import semulator.logic.label.Label;
import semulator.logic.variable.Variable;

import java.io.Serializable;
import java.util.*;

public class ProgramImpl implements Program, Serializable {

    private final String name;
    private final List<Instruction> instructions;
    private final LinkedHashSet<Variable> variables;
    private final LinkedHashSet<Label> labels;
    private final int degree;
    private final List<Program> functions;
    private final String ownerName;

    public ProgramImpl(String name, int degree, String ownerName) {
        this.name = name;
        instructions = new ArrayList<>();
        variables = new LinkedHashSet<>();
        labels = new LinkedHashSet<>();
        this.degree = degree;
        functions = new ArrayList<>();
        this.ownerName = ownerName;

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void addInstruction(Instruction instruction) {
        if (instruction == null)
            return;
        instructions.add(instruction);
        List<Variable> instructionVariables = instruction.getAllVariables();
        for (Variable variable : instructionVariables) {
            this.variables.add(variable);
        }

        List<Label> instructionLabels = instruction.getAllLabels();
        for (Label label : instructionLabels) {
            this.labels.add(label);
        }
    }

    public void addFunction(Program function){
        this.functions.add(function);
    }

    @Override
    public List<Instruction> getInstructions() {
        return instructions;
    }

    @Override
    public Validator validate() {
        String message = "";
        boolean valid = true;

        if (this == null) {
            return new Validator(false, message);
        }

        Set<String> definedLabels = new HashSet<>();
        List<Instruction> instructions = this.getInstructions();

        for (Instruction instr : instructions) {
            String rep = instr.getLabel().getLabelRepresentation();
            if (!rep.isEmpty()) {
                definedLabels.add(rep);
            }
        }

        for (Instruction instr : instructions) {
            if (instr instanceof JumpInstruction ji) {
                String target = ji.getTargetLabel().getLabelRepresentation();
                if (target.isEmpty()) {
                    valid = false;
                    message = "there is a reference in the program '" + this.name + "' to an empty Label";
                    break;
                }
                if ((!definedLabels.contains(target)) && (!target.equals("EXIT"))) {
                    valid = false;
                    message = "there is a reference in the program '" + this.name + "' to the Label '" + target + "' that doesn't exist";
                    break;
                }
            }
        }

        if(!valid) {
            return new Validator(false, message);
        }

        Validator validator;
        for (Program function : this.functions){
            validator = function.validate();
            if(!validator.isValid()){
                return new Validator(false, validator.getMessage());
            }
        }
        return new Validator(true, "success");
    }

    public Validator hasInvalidFunctionReferences(Map<String, Program> functions) {
        String message = "";
        boolean isFunctionFound;
        boolean hasInvalidFunctionReferences = false;
        List<Instruction> instructions = this.getInstructions();
        List<Program> currProgramFunctions = this.getFunctions();

        for (Instruction instr : instructions) {
            if(instr instanceof ComplexInstruction complexInstruction){
                String functionName = complexInstruction.getNameOfFunction();
                isFunctionFound = FunctionUtils.isFunctionExist(functionName, currProgramFunctions);
                if(!isFunctionFound){
                    if(!functions.containsKey(functionName)){
                        hasInvalidFunctionReferences = true;
                        message = "there is a reference to the function '" + functionName + "' but the function doesn't exist";
                        break;
                    }
                }
                List<String> funcArgs = FunctionUtils.getFunArgs(complexInstruction.getArguments());
                for(String arg : funcArgs){
                    isFunctionFound = FunctionUtils.isFunctionExist(arg, currProgramFunctions);
                    if(!isFunctionFound){
                        if(!functions.containsKey(arg)){
                            hasInvalidFunctionReferences = true;
                            message = "there is a reference to the function '" + arg + "' but the function doesn't exist";
                            break;
                        }
                    }
                }

                if(hasInvalidFunctionReferences){
                    break;
                }
            }
        }

        if(hasInvalidFunctionReferences){
            return new Validator(false, message);
        }

        return new Validator(true, "success");
    }

    @Override
    public int calculateMaxDegree() {
        if (instructions == null || instructions.isEmpty()) return 0;

        return instructions.stream()
                .mapToInt(Instruction::getExpansionDegree)
                .max()
                .orElse(0);
    }

    @Override
    public LinkedHashSet<Variable> getVariables() {
        return variables;
    }

    @Override
    public LinkedHashSet<Label> getLabels() {
        return labels;
    }

    public Program expand(int degreeOfExpand) {
        Program expandedProgram = this;
        if (degreeOfExpand == 0)
            return expandedProgram;

        long instructionNumber;
        Program nextExpandedProgram;
        Program programToExpand = expandedProgram;
        int programDegree =  degreeOfExpand;

        while(degreeOfExpand>0){
            nextExpandedProgram = new ProgramImpl(programToExpand.getName(), programDegree, this.ownerName); //new program
            Set<Integer> zUsedNumbers, usedLabelsNumbers;

            zUsedNumbers = ExpansionUtils.getSetOfUsedZNumbers(programToExpand.getVariables());
            usedLabelsNumbers = ExpansionUtils.getSetOfUsedLabels(programToExpand.getLabels());

            instructionNumber = 1;

            for(Instruction instruction : programToExpand.getInstructions()) {
                if (instruction instanceof UnexpandableInstruction unexpandableInstruction) {
                    Instruction newInstruction = unexpandableInstruction.cloneInstructionWithNewNumber(instructionNumber);
                    nextExpandedProgram.addInstruction(newInstruction);
                    instructionNumber++;
                } else if (instruction instanceof ExpandableInstruction expandableInstruction) {
                    List<Instruction> nextInstructions = expandableInstruction.expand(zUsedNumbers, usedLabelsNumbers, instructionNumber);

                    for (Instruction nextInstruction : nextInstructions) {
                        nextExpandedProgram.addInstruction(nextInstruction);
                    }
                    instructionNumber = instructionNumber + nextInstructions.size();
                }
                else{
                    if(instruction instanceof ComplexInstruction complexInstruction) {
                        Map<String,String>  oldAndNew = new HashMap<>();
                        List<Instruction> nextInstructions = complexInstruction.expand(zUsedNumbers, usedLabelsNumbers, instructionNumber, oldAndNew, null);

                        for (Instruction nextInstruction : nextInstructions) {
                            nextExpandedProgram.addInstruction(nextInstruction);
                        }
                        instructionNumber = instructionNumber + nextInstructions.size();
                    }
                }
            }
            programToExpand = nextExpandedProgram;
            degreeOfExpand--;
        }

        expandedProgram = programToExpand;

        List<Program> functions = this.getFunctions();
        for (Program function : functions) {
            if(expandedProgram instanceof ProgramImpl expandedProgramImpl)
                expandedProgramImpl.addFunction(function);
        }
        return expandedProgram;
    }

    public int getDegree() {
        return degree;
    }

    public List<Program> getFunctions() {
        return functions;
    }

    @Override
    public int findMaxDepth(){
        List<Instruction> instructions = this.getInstructions();
        List<Program> functions = this.getFunctions();
        int maxDepth = 0, currentDepth;
        for (Instruction instruction : instructions) {
            if(instruction instanceof ComplexInstruction complexInstruction) {
                currentDepth = complexInstruction.findDepthOfFunction();
                if(currentDepth > maxDepth) {
                    maxDepth = currentDepth;
                }
            }
        }

        for(Program function : functions){
            currentDepth = function.findMaxDepth();
            if(currentDepth > maxDepth) {
                maxDepth = currentDepth;
            }
        }
        return maxDepth;
    }

    @Override
    public String getUsername(){
        return this.ownerName;
    }

    @Override
    public Program expand(int desiredDegreeOfExpand, Map<String, Program> functions){
        Program expandedProgram = this;
        if (desiredDegreeOfExpand == 0)
            return expandedProgram;

        long instructionNumber;
        Program nextExpandedProgram;
        Program programToExpand = expandedProgram;
        int programDegree =  desiredDegreeOfExpand;

        while(desiredDegreeOfExpand>0){
            nextExpandedProgram = new ProgramImpl(programToExpand.getName(), programDegree, this.ownerName); //new program
            Set<Integer> zUsedNumbers, usedLabelsNumbers;

            zUsedNumbers = ExpansionUtils.getSetOfUsedZNumbers(programToExpand.getVariables());
            usedLabelsNumbers = ExpansionUtils.getSetOfUsedLabels(programToExpand.getLabels());

            instructionNumber = 1;

            for(Instruction instruction : programToExpand.getInstructions()) {
                if (instruction instanceof UnexpandableInstruction unexpandableInstruction) {
                    Instruction newInstruction = unexpandableInstruction.cloneInstructionWithNewNumber(instructionNumber);
                    nextExpandedProgram.addInstruction(newInstruction);
                    instructionNumber++;
                } else if (instruction instanceof ExpandableInstruction expandableInstruction) {
                    List<Instruction> nextInstructions = expandableInstruction.expand(zUsedNumbers, usedLabelsNumbers, instructionNumber);

                    for (Instruction nextInstruction : nextInstructions) {
                        nextExpandedProgram.addInstruction(nextInstruction);
                    }
                    instructionNumber = instructionNumber + nextInstructions.size();
                }
                else{
                    if(instruction instanceof ComplexInstruction complexInstruction) {
                        Map<String,String>  oldAndNew = new HashMap<>();
                        List<Instruction> nextInstructions = complexInstruction.expand(zUsedNumbers, usedLabelsNumbers, instructionNumber, oldAndNew, functions);

                        for (Instruction nextInstruction : nextInstructions) {
                            nextExpandedProgram.addInstruction(nextInstruction);
                        }
                        instructionNumber = instructionNumber + nextInstructions.size();
                    }
                }
            }
            programToExpand = nextExpandedProgram;
            desiredDegreeOfExpand--;
        }

        expandedProgram = programToExpand;

        if(expandedProgram instanceof ProgramImpl expandedProgramImpl){
            List<Program> parentFunctions = this.getFunctions();
            for (Program function : parentFunctions) {
                expandedProgramImpl.addFunction(function);
            }
        }

        return expandedProgram;
    }
}