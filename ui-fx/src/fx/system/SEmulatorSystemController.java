package fx.system;

import fx.app.util.DisplayUtils;
import fx.app.util.ProgramUtil;
import fx.component.execution.DebuggerExecutionController;
import fx.component.history.HistoryController;
import fx.component.instructions.InstructionPaneController;
import fx.component.topbar.TopBarController;
import jakarta.xml.bind.JAXBException;
import javafx.fxml.FXML;
import semulator.api.LoadReport;
import semulator.api.dto.ExecutionRunDto;
import semulator.api.dto.InstructionDto;
import semulator.api.dto.ProgramDto;
import semulator.api.dto.ProgramFunctionDto;
import semulator.core.SEmulatorEngine;
import semulator.core.SEmulatorEngineImpl;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;

public class SEmulatorSystemController {

    private SEmulatorEngine engine = new SEmulatorEngineImpl();
    @FXML private TopBarController topBarController;
    @FXML private InstructionPaneController instructionsController;
    @FXML private DebuggerExecutionController debuggerController;
    @FXML private HistoryController historyController;

    private String currentProgramName;

    @FXML
    public void initialize() {
        if (topBarController != null) {
            topBarController.setMainController(this);
        }
        if (instructionsController != null) {
            instructionsController.setMainController(this);
        }
        if (debuggerController != null) {
            debuggerController.setMainController(this);
        }
        if (historyController != null) {
            historyController.setMainController(this);
        }
    }

    public void btnLoadFileListener(String fileName) {
        LoadReport loadReport = null;
        Path path;
        path = Paths.get(fileName);

        try {
            loadReport = engine.loadProgramDetails(path);
        }
        catch(JAXBException e){

        }
        if(loadReport == null){
            return;
        }
        if(loadReport.isSuccess()) {
            ProgramFunctionDto programInContextDetails = engine.displayProgram();
            if(programInContextDetails!=null) {
                if(currentProgramName==null) {
                    currentProgramName = programInContextDetails.getName();
                }
                instructionsController.displayProgram(programInContextDetails);

                int programDegree = ProgramUtil.getDisplayedProgramDegree(programInContextDetails);
                int maxDegree = ProgramUtil.getDisplayedProgramMaxDegree(programInContextDetails);

                topBarController.updateDegreeLabel(programDegree, maxDegree);


                topBarController.refreshHighlightOptions(programInContextDetails);

                debuggerController.setProgram(programInContextDetails);
                topBarController.setLoadFileText(fileName);
                List<String> programOrFunctionOptions = engine.getProgramOrFunctionNames();
                topBarController.refreshProgramOrFunctionOptions(programOrFunctionOptions);
            }
        }
    }

    public void btnExpandListener(int degreeToExpand) {
        ProgramFunctionDto expandedProgramDetails = engine.expand(degreeToExpand);
        if(expandedProgramDetails != null) {
            instructionsController.displayProgram(expandedProgramDetails);
            topBarController.refreshHighlightOptions(expandedProgramDetails);
            javafx.application.Platform.runLater(() -> {
                topBarController.updateCurrentDegreeLabel(degreeToExpand);
            });
            debuggerController.setProgram(expandedProgramDetails);
        }
    }

    public void btnCollapseListener(int degreeToCollapse) {
        ProgramFunctionDto collapsedProgramDetails = engine.expand(degreeToCollapse);
        if(collapsedProgramDetails != null) {
            instructionsController.displayProgram(collapsedProgramDetails);
            topBarController.refreshHighlightOptions(collapsedProgramDetails);
            debuggerController.setProgram(collapsedProgramDetails);
            javafx.application.Platform.runLater(() -> {
                topBarController.updateCurrentDegreeLabel(degreeToCollapse);
            });
        }
    }

    public void onHighlightChangedListener(String highlightSelected){
        instructionsController.highlightSelectionOnTable(highlightSelected);
    }




    public void btnRunListener(long... inputs){
        int degreeOfRun = topBarController.getCurrentDegree();
        ExecutionRunDto runResult= engine.runProgram(degreeOfRun,inputs);
        if(runResult!=null){
            debuggerController.updateRunResult(runResult);
        }
    }

    public void onProgramFunctionChangedListener(String programFunctionSelected){
        if (programFunctionSelected == null || programFunctionSelected.isBlank())
            return;
        engine.setProgramInContext(programFunctionSelected);
        ProgramFunctionDto programInContextDetails = engine.displayProgram();
        instructionsController.displayProgram(programInContextDetails);
        int programDegree = ProgramUtil.getDisplayedProgramDegree(programInContextDetails);
        int maxDegree = ProgramUtil.getDisplayedProgramMaxDegree(programInContextDetails);
        topBarController.updateDegreeLabel(programDegree, maxDegree);
        topBarController.refreshHighlightOptions(programInContextDetails);
        debuggerController.setProgram(programInContextDetails);
    }

}
