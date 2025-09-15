package semulator.logic.program;

import semulator.logic.Function.Function;
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

    public ProgramImpl(String name, int degree) {
        this.name = name;
        instructions = new ArrayList<>();
        variables = new LinkedHashSet<>();
        labels = new LinkedHashSet<>();
        this.degree = degree;
        functions = new ArrayList<>();

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

    public boolean hasInvalidFunctionReferences(){
        boolean isFunctionFound;
        boolean hasInvalidFunctionReferences = false;
        List<Instruction> instructions = this.getInstructions();
        List<Program> functions = this.getFunctions();

        for (Instruction instr : instructions) {
            if(instr instanceof QuoteInstruction quoteInstruction){
                String functionName = quoteInstruction.getFunctionName();
                isFunctionFound = FunctionUtils.isFunctionExist(functionName, functions);
                if(!isFunctionFound){
                    hasInvalidFunctionReferences = true;
                    break;
                }

            }
        }
        return hasInvalidFunctionReferences;
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
            nextExpandedProgram = new ProgramImpl(programToExpand.getName(), programDegree); //new program
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
                        List<Instruction> nextInstructions = complexInstruction.expand(zUsedNumbers, usedLabelsNumbers, instructionNumber, oldAndNew, this);

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
}