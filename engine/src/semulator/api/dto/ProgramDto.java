package semulator.api.dto;

import semulator.logic.Function.Function;
import semulator.logic.instruction.Instruction;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.label.LabelImpl;
import semulator.logic.program.Program;
import semulator.logic.program.ProgramImpl;
import semulator.logic.variable.Variable;
import semulator.logic.variable.VariableType;

import java.util.*;

public class ProgramDto implements ProgramFunctionDto{
    private final String programName;
    private final List<InstructionDto> instructions;
    private final List<String> inputVariablesInOrder;
    private final List<String> allVariablesInOrder;
    private final List<String> labelsInOrder;
    private final int programDegree;
    private final int maxDegree;
    private final List<FunctionDto> functions;


    public ProgramDto(Program program, Map<String, Program> functions) {
        this.programName = program.getName();

        this.inputVariablesInOrder =  new ArrayList<>();
        this.labelsInOrder = new ArrayList<>();
        this.allVariablesInOrder = new ArrayList<>();
        this.functions = new ArrayList<>();

        List<Integer> labelsNumbers = new ArrayList<>();
        List<Integer> inputVariablesNumbers = new ArrayList<>();
        List<Integer> workVariablesNumbers = new ArrayList<>();

        LinkedHashSet<Variable> allVars = program.getVariables();
        LinkedHashSet<Label> labels = program.getLabels();

        boolean sawExit = false;
        boolean sawY = false;

        for(Label label : labels) {
            if (label instanceof LabelImpl labelImpl)
                labelsNumbers.add(labelImpl.getNumber());
            if (label == FixedLabel.EXIT)
                sawExit = true;
        }

        Collections.sort(labelsNumbers);
        for(Integer number : labelsNumbers) {
            labelsInOrder.add(("L" + number));
        }

        if (sawExit)
            labelsInOrder.add(FixedLabel.EXIT.getLabelRepresentation());


        for(Variable variable : allVars) {
            if(variable!=null){
                if (variable.getType() == VariableType.INPUT) {
                    inputVariablesNumbers.add(variable.getNumber());
                }
                if (variable.getType() == VariableType.WORK) {
                    workVariablesNumbers.add(variable.getNumber());
                }
                if (variable==Variable.RESULT) {
                    sawY = true;
                }
            }
        }

        Collections.sort(inputVariablesNumbers);
        Collections.sort(workVariablesNumbers);

        if(sawY){
            allVariablesInOrder.add("y");
        }

        for(Integer number : inputVariablesNumbers) {
            inputVariablesInOrder.add(("x" + number));
            allVariablesInOrder.add(("x" + number));
        }

        for(Integer number : workVariablesNumbers) {
            allVariablesInOrder.add(("z" + number));
        }

        List<Instruction> programInstructions = program.getInstructions();
        this.instructions = new ArrayList<>();

        for (Instruction instruction : programInstructions) {
            this.instructions.add(new InstructionDto(instruction, functions));
        }

        this.programDegree = program.getDegree();

        ProgramImpl  programImpl = (ProgramImpl) program;
        List<Program> programFunctions = programImpl.getFunctions();

        for (Program function : programFunctions) {
            FunctionDto newFunctionDto = new FunctionDto(function, functions);
            this.functions.add(newFunctionDto);
        }

        this.maxDegree = program.calculateMaxDegree();
    }

    @Override
    public String getName() {
        return programName;
    }

    @Override
    public List<String> getInputVariablesInOrder() {
        return inputVariablesInOrder;
    }

    @Override
    public List<String> getLabelsInOrder() {
        return labelsInOrder;
    }

    @Override
    public List<InstructionDto> getInstructions() {
        return instructions;
    }

    @Override
    public int getDegree() {
        return programDegree;
    }

    @Override
    public List<String> getAllVariablesInOrder() {
        return allVariablesInOrder;
    }

    public List<FunctionDto> getFunctions() {
        return functions;
    }

    @Override
    public int getMaxDegree() {
        return maxDegree;
    }
}

