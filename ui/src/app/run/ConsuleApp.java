package app.run;

import app.display.DisplayManager;
import app.menu.Command;
import app.menu.Menu;
import semulator.core.loader.LoadReport;
import semulator.logic.execution.ExecutionRunDto;
import semulator.logic.program.ProgramDto;
import validation.UserInputHandler;
import semulator.core.SEmulatorEngine;
import semulator.core.SEmulatorEngineImpl;
import jakarta.xml.bind.JAXBException;

import java.nio.file.Path;
import java.util.List;

public class ConsuleApp {
    private final SEmulatorEngine semulatorEngine;
    private boolean isExit;

    public ConsuleApp() {
        this.semulatorEngine = new SEmulatorEngineImpl();
        this.isExit = false;
    }
    public void runApp(){
        Command choice;
        String lineSeparator = System.lineSeparator();

        while(!this.isExit){
            Menu.printMenu();
            choice = UserInputHandler.getUserChoice();
            if((choice != Command.EXIT && choice != Command.LOAD_XML) && (!this.semulatorEngine.isLoaded())){
                System.out.println("This option is available only after successfully loading a program into the system (command 1).");
                System.out.print(lineSeparator);
            }
            else
                this.isExit = handleCommand(choice);
        }
    }

    private boolean handleCommand(Command command){
        boolean isExit = false;
        int desiredDegreeOfExpand, maxDegreeOfExpand;
        String lineSeparator = System.lineSeparator();

        switch (command){
            case Command.EXIT:
                isExit = true;
                break;
            case Command.LOAD_XML:
                System.out.print(lineSeparator);
                System.out.println("******************************* Load program **********************************");
                Path filePath;
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
                }
                catch (JAXBException e) {
                    System.out.println("Unexpected error while loading XML: " + e.getMessage());
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
        }

        return isExit;
    }
}