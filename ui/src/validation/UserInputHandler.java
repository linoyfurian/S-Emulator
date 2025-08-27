package validation;

import app.menu.Command;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import static java.lang.System.in;

public class UserInputHandler {
    private static final Scanner scanner = new Scanner(in);

    public static Command getUserChoice() {
        Command userChoice = null;

        while (true) {
            System.out.print("Enter your choice (1-6): ");
            String choiceInput = scanner.nextLine().trim();

            int choice;
            try {
                choice = Integer.parseInt(choiceInput);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number between 1 and 6. Please try again.");
                continue;
            }
            if (choice < 1 || choice > 6) {
                System.out.println("Error: choice must be a number between 1 and 6. Please try again.");
                continue;
            }

            Command cmd = Command.fromCode(choice);
            if (cmd == null) {
                System.out.println("Unexpected error mapping choice. Please try again.");
                continue;
            }
            return cmd;
        }
    }

    public static Path readFullPathFromUser() {
        Path path = null;
        System.out.print("Enter full XML file path: ");
        String inputPath = scanner.nextLine().trim();

        if (inputPath.isEmpty())
            throw new IllegalArgumentException("Error: path cannot be empty. You need to enter a full path.");
        if (!inputPath.matches("[A-Za-z0-9\\\\/.:_\\- ]+"))
            throw new IllegalArgumentException("Error: path contains invalid characters. The path must use English letters.");
        try {
            path = Paths.get(inputPath);
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException("Error: invalid path syntax. You need to enter a valid full path.");
        }
        if (!path.isAbsolute())
            throw new IllegalArgumentException("Error: You need to enter a full (absolute) path");

        return path;
    }

    public static int getDegreeFromUser(int maxDegreeOfExpand) {
        int desiredDegreeOfExpand;
        while (true) {
            System.out.println("Max Degree of Expand: " + maxDegreeOfExpand);
            if(maxDegreeOfExpand == 0)
                System.out.print("Only expansion 0 is available. Please enter 0 to display the program: ");
            else
                System.out.print("Please enter desired expand degree (0 - " + maxDegreeOfExpand + "): ");

            String input = scanner.nextLine().trim();

            try {
                desiredDegreeOfExpand = Integer.parseInt(input);

                if (desiredDegreeOfExpand >= 0 && desiredDegreeOfExpand <= maxDegreeOfExpand) {
                    break;
                } else {
                    System.out.println("Invalid number. Must be between 0 and " + maxDegreeOfExpand + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }


        }
        return desiredDegreeOfExpand;
    }

    public static long[] getRunInputsFromUser() {
        while (true) {
            System.out.println("Please enter the input values as a list of non-negative integers separated by commas (,)");
            System.out.println("(You can enter less or more inputs than needed. Unused inputs will be ignored, and missing inputs will be treated as 0): ");

            String userInput = scanner.nextLine().trim();

            if (userInput.isEmpty()) {
                return new long[0];
            }

            String[] parts = userInput.split(",");
            long[] values = new long[parts.length];
            boolean validInputValue = true;

            for (int i = 0; i < parts.length; i++) {
                String inputValueStr = parts[i].trim();
                if (inputValueStr.isEmpty() || !inputValueStr.matches("\\d+")) {
                    validInputValue = false;
                    break;
                }
                try {
                    long value = Long.parseLong(inputValueStr);
                    if (value < 0) {
                        validInputValue = false;
                        break;
                    }
                    values[i] = value;
                } catch (NumberFormatException ex) {
                    validInputValue = false;
                    break;
                }
            }

            if (validInputValue) {
                return values;
            } else {
                System.out.println("Input is invalid. Please enter non-negative integers separated by commas (for example: 1,2,3)");
            }
        }
    }
}