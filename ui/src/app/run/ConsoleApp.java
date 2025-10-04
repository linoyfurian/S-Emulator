package app.run;

import app.display.DisplayManager;
import app.menu.Command;
import app.menu.Menu;
import semulator.api.dto.ExecutionRunDto;
import semulator.api.dto.ProgramDto;
import validation.UserInputHandler;
import semulator.core.SEmulatorEngine;
import semulator.core.SEmulatorEngineImpl;
import jakarta.xml.bind.JAXBException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class ConsoleApp {
    private final SEmulatorEngine semulatorEngine;
    private boolean isExit;

    public ConsoleApp() {
        this.semulatorEngine = new SEmulatorEngineImpl();
        this.isExit = false;
    }
    public void runApp(){
        Command choice;
        String lineSeparator = System.lineSeparator();

        while(!this.isExit){
            Menu.printMenu();
            choice = UserInputHandler.getUserChoice();
            if(choice == Command.LOAD_STATE)
                this.isExit = handleCommand(choice);
            else if((choice != Command.EXIT && choice != Command.LOAD_XML) && (!this.semulatorEngine.isLoaded())){
                System.out.println("This option is available only after successfully loading a program into the system (command 1).");
                System.out.print(lineSeparator);
            }
            else
                this.isExit = handleCommand(choice);
        }
        System.out.print(lineSeparator);
        System.out.println("Exiting...");
    }

    private boolean handleCommand(Command command){
        boolean isExit = false;
        int desiredDegreeOfExpand, maxDegreeOfExpand;
        String lineSeparator = System.lineSeparator();

        switch (command) {
            case Command.EXIT:
                isExit = true;
                break;
            case Command.LOAD_XML:
                System.out.print(lineSeparator);
                System.out.println("******************************* Load program **********************************");
                Path filePath;
                try {
                    filePath = UserInputHandler.readFullPathFromUser();
                    try {
                        LoadReport loadReport = semulatorEngine.loadProgramDetails(filePath);

                        if (loadReport.isSuccess()) {
                            System.out.println(loadReport.getMessage());
                            this.semulatorEngine.setLoaded(true);
                            this.semulatorEngine.resetProgramRuns();
                        } else {
                            System.out.println("Program loading failed: " + loadReport.getMessage());
                            System.out.println("If you want to try again, select option 1 in the menu.");
                        }
                    } catch (JAXBException e) {
                        System.out.println("Unexpected error while loading XML: " + e.getMessage());
                        System.out.println("If you want to try again, select option 1 in the menu.");
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                    System.out.println("If you want to try again, select option 1 in the menu.");
                }
                System.out.print(lineSeparator);
                break;
            case Command.DISPLAY_PROGRAM:
                System.out.print(lineSeparator);
                System.out.println("****************************** Display program ********************************");
                ProgramDto programInfoToDisplay = this.semulatorEngine.displayProgram();
                DisplayManager.displayProgram(programInfoToDisplay);
                System.out.print(lineSeparator);
                break;
            case Command.EXPAND:
                System.out.print(lineSeparator);
                System.out.println("****************************** Expand program *********************************");
                maxDegreeOfExpand = this.semulatorEngine.getMaxDegreeOfExpand();
                desiredDegreeOfExpand = UserInputHandler.getDegreeFromUser(maxDegreeOfExpand);
                ProgramDto expandedProgramDetails = this.semulatorEngine.expand(desiredDegreeOfExpand);
                DisplayManager.displayProgram(expandedProgramDetails);
                System.out.print(lineSeparator);
                break;
            case Command.RUN_PROGRAM:
                System.out.print(lineSeparator);
                System.out.println("******************************** Run program **********************************");
                System.out.println("To run the program " + this.semulatorEngine.getProgramName() + " you need to choose degree of expand to run the program in.");
                maxDegreeOfExpand = this.semulatorEngine.getMaxDegreeOfExpand();
                desiredDegreeOfExpand = UserInputHandler.getDegreeFromUser(maxDegreeOfExpand);

                ProgramDto programToRunDetails = this.semulatorEngine.expand(desiredDegreeOfExpand);
                System.out.print("These are the inputs of the program: ");
                List<String> sortedInputVariables = programToRunDetails.getInputVariablesInOrder();
                DisplayManager.displayInputVariables(sortedInputVariables);

                long[] inputs = UserInputHandler.getRunInputsFromUser();

                ExecutionRunDto runResult = this.semulatorEngine.runProgram(desiredDegreeOfExpand, inputs);

                DisplayManager.displayProgram(programToRunDetails);
                DisplayManager.displayRunDetails(runResult);
                System.out.print(lineSeparator);
                break;
            case Command.DISPLAY_HISTORY:
                System.out.print(lineSeparator);
                System.out.println("********************** Display program's run history **************************");
                List<ExecutionRunDto> historyOfProgramRuns = this.semulatorEngine.historyDisplay();
                DisplayManager.displayRunHistory(historyOfProgramRuns);
                break;
            case Command.SAVE_STATE:
                System.out.print(lineSeparator);
                System.out.println("******************** Save current emulator state to file **********************");

                Path pathToSave;
                try {
                    pathToSave = UserInputHandler.readFullPathFromUserToSaveOrToLoadState();
                    try {
                        this.semulatorEngine.saveState(pathToSave);
                        System.out.println("The current system state was successfully saved to the requested file.");
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                        System.out.println("If you want to try again, select option 6 in the menu.");
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        System.out.println("If you want to try again, select option 6 in the menu.");
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                    System.out.println("If you want to try again, select option 6 in the menu.");
                }
                System.out.print(lineSeparator);
                break;
            case Command.LOAD_STATE:
                System.out.print(lineSeparator);
                System.out.println("************************* Load emulator state from file ***********************");

                Path pathToLoadFrom;
                try {
                    pathToLoadFrom = UserInputHandler.readFullPathFromUserToSaveOrToLoadState();
                    try {
                        this.semulatorEngine.loadState(pathToLoadFrom);
                        System.out.println("The system state was successfully loaded from the requested file.");
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                        System.out.println("If you want to try again, select option 7 in the menu.");
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        System.out.println("If you want to try again, select option 7 in the menu.");
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                    System.out.println("If you want to try again, select option 7 in the menu.");
                }
                System.out.print(lineSeparator);
                break;
        }
        return isExit;
    }
}