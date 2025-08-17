package semulator.logic.program;

import semulator.logic.instruction.Instruction;
import semulator.logic.label.Label;
import semulator.logic.variable.Variable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class ProgramImpl implements Program {

    private final String name;
    private final List<Instruction> instructions;
    private final LinkedHashSet<Variable> variables;
    private final LinkedHashSet<Label> labels;

    public ProgramImpl(String name) {
        this.name = name;
        instructions = new ArrayList<>();
        variables = new LinkedHashSet<>();
        labels = new LinkedHashSet<>();

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
        return false;
    }

    /// to impl

    @Override
    public int calculateMaxDegree() {
        if (instructions == null || instructions.isEmpty()) return 0;

        return instructions.stream()
                .mapToInt(Instruction::getExpansionDegree)
                .max()
                .orElse(0);
    }

    @Override
    public int calculateCycles() {
        if (instructions == null || instructions.isEmpty()) return 0;

        return instructions.stream()
                .mapToInt(Instruction::cycles)
                .sum();
    }

    @Override
    public LinkedHashSet<Variable> getVariables() {
        return variables;
    }

    @Override
    public LinkedHashSet<Label> getLabels() {
        return labels;
    }

}