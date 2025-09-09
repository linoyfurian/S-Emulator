package semulator.api.dto;

import semulator.logic.Function.Function;
import semulator.logic.instruction.Instruction;
import semulator.logic.label.FixedLabel;
import semulator.logic.label.Label;
import semulator.logic.label.LabelImpl;
import semulator.logic.program.Program;
import semulator.logic.variable.Variable;
import semulator.logic.variable.VariableType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;

public class FunctionDto implements ProgramFunctionDto{
    private final String functionName;
    private final List<InstructionDto> instructions;
    private final List<String> inputVariablesInOrder;
    private final List<String> allVariablesInOrder;
    private final List<String> labelsInOrder;
    private final int functionDegree;
    private final int maxDegree;
    private final String userString;

    public FunctionDto(Program function, Program program) {
        Function functionToDto = (Function) function;

        this.functionName = function.getName();

        this.inputVariablesInOrder =  new ArrayList<>();
        this.labelsInOrder = new ArrayList<>();
        this.allVariablesInOrder = new ArrayList<>();

        List<Integer> labelsNumbers = new ArrayList<>();
        List<Integer> inputVariablesNumbers = new ArrayList<>();
        List<Integer> workVariablesNumbers = new ArrayList<>();

        LinkedHashSet<Variable> allVars = function.getVariables();
        LinkedHashSet<Label> labels = function.getLabels();

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

        List<Instruction> functionInstruction = function.getInstructions();
        this.instructions = new ArrayList<>();

        for (Instruction instruction : functionInstruction) {
            this.instructions.add(new InstructionDto(instruction, program));
        }

        this.functionDegree = function.getDegree();
        this.userString = functionToDto.getUserString();

        this.maxDegree = function.calculateMaxDegree();
    }

    @Override
    public String getName() {
        return functionName;
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
        return functionDegree;
    }

    @Override
    public List<String> getAllVariablesInOrder() {
        return allVariablesInOrder;
    }

    public String getUserString() {
        return userString;
    }

    @Override
    public int getMaxDegree() {
        return maxDegree;
    }
}
