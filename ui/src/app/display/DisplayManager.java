package app.display;

import semulator.logic.execution.ExecutionRunDto;
import semulator.logic.instruction.InstructionDto;
import semulator.logic.instruction.ParentInstructionDto;
import semulator.logic.program.ProgramDto;

import java.util.*;

public class DisplayManager {
    public static void displayProgram(ProgramDto programToDisplay) {
        boolean firstInLine = true;
        List<String> inputVariablesInOrder, labelsInOrder;
        List<InstructionDto> instructionsToDisplay;

        String lineSeparator = System.lineSeparator();
        System.out.println("Program name: " + programToDisplay.getProgramName());

        inputVariablesInOrder = programToDisplay.getInputVariablesInOrder();
        System.out.print("Input variables in order: ");
        if(inputVariablesInOrder.isEmpty()) {
            System.out.println("no input variables");
        }
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
        System.out.print("Labels in order: ");
        if(labelsInOrder.isEmpty()) {
            System.out.println("no labels");
        }
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
        System.out.println("Run result: y = " + runResult.getResult());

        LinkedHashMap<String, Long> programVariable = runResult.getVariables();
        printVariablesInFormat(programVariable);

        System.out.println("Number of cycles: " + runResult.getCycles());
    }

    private static void printVariablesInFormat(LinkedHashMap<String, Long> variables) {
        System.out.println("Program variables after program run: ");
        for (var entry : variables.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());

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
            System.out.print(lineSeparator);
            return;
        }

        System.out.println("Execution History:");
        System.out.println("********************");
        for (ExecutionRunDto run : historyOfProgramRuns) {
            System.out.println("Run number: #" + run.getRunNumber());
            System.out.println("Run degree: " + run.getExpansionDegree());
            long [] inputs = run.getInputs();
            System.out.print("Inputs: ");

            if(inputs.length == 0)
                System.out.print("No input data");
            System.out.print(lineSeparator);

            for(int i = 0; i < inputs.length; i++){
                System.out.println("x" + (i+1) + " = " + inputs[i]);
            }

            System.out.println("Result: y = " + run.getResult());
            System.out.println("Cycles: " + run.getCycles());
            System.out.print(lineSeparator);
        }
        System.out.print(lineSeparator);
    }
}