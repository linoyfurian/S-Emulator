package app.display;

import semulator.logic.execution.ExecutionRunDto;
import semulator.logic.instruction.InstructionDto;
import semulator.logic.instruction.ParentInstructionDto;
import semulator.logic.program.ProgramDto;
import semulator.logic.variable.Variable;
import semulator.logic.variable.VariableType;

import java.sql.SQLOutput;
import java.util.*;
import java.util.stream.Collectors;

public class DisplayManager {
    public static void displayProgram(ProgramDto programToDisplay) {
        boolean firstInLine = true;
        List<String> inputVariablesInOrder, labelsInOrder;
        List<InstructionDto> instructionsToDisplay;

        String lineSeparator = System.lineSeparator();
        System.out.println("Program name: " + programToDisplay.getProgramName());

        inputVariablesInOrder = programToDisplay.getInputVariablesInOrder();
        System.out.println("Input variables in order: ");
        for (String variableName : inputVariablesInOrder) {
            if (firstInLine) {
                System.out.print(variableName);
                firstInLine = false;
            } else
                System.out.print(", " + variableName);
        }
        System.out.print(lineSeparator);

        firstInLine = true;
        labelsInOrder = programToDisplay.getLabelsInOrder();
        System.out.println("Labels in order: ");
        for (String labelName : labelsInOrder) {
            if (firstInLine) {
                System.out.print(labelName);
                firstInLine = false;
            } else
                System.out.print(", " + labelName);
        }

        System.out.print(lineSeparator);

        instructionsToDisplay = programToDisplay.getInstructions();
        System.out.println("Program's instructions: ");
        for (InstructionDto instruction : instructionsToDisplay) {
            displayInstructionByFormat(instruction);
            System.out.print(lineSeparator);
        }
    }

    private static void displayInstructionByFormat(InstructionDto instructionToDisplay) {
        // #<NUMBER> (B/S) [LABEL] <COMMAND> (CYCLES)
        String formattedInstruction, formattedParentInstruction;
        formattedInstruction = String.format(
                "#%d (%s) [%s] %s (%d)",
                instructionToDisplay.getNumber(),
                instructionToDisplay.getType(),
                formatLabel(instructionToDisplay.getLabel()),
                instructionToDisplay.getCommand(),
                instructionToDisplay.getCycles());

        System.out.print(formattedInstruction);

        List<ParentInstructionDto> parentsOfInstruction = instructionToDisplay.getParents();
        for (ParentInstructionDto parent : parentsOfInstruction) {
            formattedParentInstruction = String.format(
                    " <<< #%d (%s) [%s] %s (%d)",
                    parent.getNumber(),
                    parent.getType(),
                    formatLabel(parent.getLabel()),
                    parent.getCommand(),
                    parent.getCycles());

            System.out.print(formattedParentInstruction);
        }
    }

    private static String formatLabel(String label) {
        if (label == null || label.isBlank()) {
            return String.format("%-5s", "");
        }
        return String.format("%-5s", label);
    }

    public static void displayRunDetails(ExecutionRunDto runResult) {
        String lineSeparator = System.lineSeparator();

        System.out.println("y = " + runResult.getResult());

        LinkedHashMap<String, Long> programVariable = runResult.getVariables();
        printVariablesInOneLineInFormat(programVariable);
        System.out.print(lineSeparator);

        System.out.println("Number of cycles: " + runResult.getCycles());
    }

    private static void printVariablesInOneLineInFormat(LinkedHashMap<String, Long> variables) {
        boolean firstInLine = true;
        for (var entry : variables.entrySet()) {
            if (firstInLine) {
                System.out.print(entry.getKey() + " = " + entry.getValue());
                firstInLine = false;
            } else
                System.out.print(", " + entry.getKey() + " = " + entry.getValue());
        }
    }

    public static void displayInputVariables(List<String> sortedInputVariables) {
        boolean isFirstVariable = true;
        String lineSeparator = System.lineSeparator();

        for (String variableName : sortedInputVariables) {
            if (isFirstVariable) {
                System.out.print(variableName);
                isFirstVariable = false;
            }
            else
                System.out.print(", " + variableName);
        }
        System.out.print(lineSeparator);
    }

    public static void displayRunHistory(List<ExecutionRunDto> historyOfProgramRuns){
        String lineSeparator = System.lineSeparator();

        if(historyOfProgramRuns.size() == 0){
            System.out.println("No runs have been executed for this program yet");
            return;
        }

        System.out.println("Execution History:");
        for (ExecutionRunDto run : historyOfProgramRuns) {
            System.out.println("Run number: #" + run.getRunNumber());
            System.out.println("Run degree: " + run.getExpansionDegree());
            long [] inputs = run.getInputs();
            System.out.print("Inputs: ");
            boolean firstInLine = true;
            for (long input : inputs) {
                if (firstInLine) {
                    System.out.print(input);
                    firstInLine = false;
                }
                else
                    System.out.print(", " + input);

            }
            System.out.print(lineSeparator);
            System.out.println("y = " + run.getResult());
            System.out.println("Cycles: " + run.getCycles());
        }
        System.out.print(lineSeparator);
    }
}