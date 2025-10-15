package semulator.logic.Function;

import semulator.logic.instruction.*;
import semulator.logic.instruction.expansion.ExpansionUtils;
import semulator.logic.label.Label;
import semulator.logic.program.Program;
import semulator.logic.program.ProgramImpl;
import semulator.logic.variable.Variable;

import java.io.Serializable;
import java.util.*;

public class Function implements Program, Serializable {
    private final String name;
    private final List<Instruction> instructions;
    private final LinkedHashSet<Variable> variables;
    private final LinkedHashSet<Label> labels;
    private final int degree;
    private final String userString;
    private final String programParent;
    private final String ownerName;

    public Function(String name, String userString, int degree, String programParent, String ownerName) {
        this.name = name;
        this.userString = userString;
        instructions = new ArrayList<>();
        variables = new LinkedHashSet<>();
        labels = new LinkedHashSet<>();
        this.degree = degree;
        this.programParent = programParent;
        this.ownerName = ownerName;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void addInstruction(Instruction instruction) {
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

    @Override
    public List<Instruction> getInstructions() {
        return instructions;
    }

    @Override
    public boolean validate() {
        boolean valid = true;

        if (this == null) {
            return false;
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
                    break;
                }
                if ((!definedLabels.contains(target)) && (!target.equals("EXIT"))) {
                    valid = false;
                    break;
                }
            }
        }
        return valid;
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

    public Program expand(int degreeOfExpand, Program parentProgram) {
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

        ProgramImpl parentProgramImpl = (ProgramImpl) parentProgram;
        List<Program> functions = parentProgramImpl.getFunctions();
        for (Program function : functions) {
            if(expandedProgram instanceof ProgramImpl expandedProgramImpl)
                expandedProgramImpl.addFunction(function);
        }
        return expandedProgram;
    }

    public int getDegree() {
        return degree;
    }

    public String getUserString() {
        return userString;
    }

    public boolean hasInvalidFunctionReferences(List<Program> functions, Map<String, Program> allFunctions) {
        boolean isFunctionFound;
        boolean hasInvalidFunctionReferences = false;
        List<Instruction> instructions = this.getInstructions();

        for (Instruction instr : instructions) {
            if(instr instanceof QuoteInstruction quoteInstruction){
                String functionName = quoteInstruction.getFunctionName();
                isFunctionFound = FunctionUtils.isFunctionExist(functionName, functions);
                if(!isFunctionFound){
                    if(!allFunctions.containsKey(functionName)){
                        hasInvalidFunctionReferences = true;
                        break;
                    }
                }
            }
        }
        return hasInvalidFunctionReferences;
    }

    @Override
    public int findMaxDepth(){
        List<Instruction> instructions = this.getInstructions();

        int maxDepth = 0, currentDepth;
        for (Instruction instruction : instructions) {
            if(instruction instanceof ComplexInstruction complexInstruction) {
                currentDepth = complexInstruction.findDepthOfFunction();
                if(currentDepth > maxDepth) {
                    maxDepth = currentDepth;
                }
            }
        }
        return maxDepth;
    }

    public String getProgramParent(){
        return programParent;
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
        return expandedProgram;
    }
}