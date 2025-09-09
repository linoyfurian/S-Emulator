package fx.system;

import fx.app.util.DisplayUtils;
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
        Path path = null;
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
            ProgramDto programDetails = engine.displayProgram();
            if(programDetails!=null) {
                instructionsController.displayProgram(programDetails);
                int programDegree = programDetails.getProgramDegree();
                int maxDegree = engine.getMaxDegreeOfExpand();
                topBarController.updateDegreeLabel(programDegree, maxDegree);
                topBarController.refreshHighlightOptions(programDetails);
                debuggerController.setProgram(programDetails);
                topBarController.setLoadFileText(fileName);
                topBarController.refreshProgramOrFunctionOptions(programDetails);
            }
        }
    }

    public void btnExpandListener(int degreeToExpand) {
        ProgramDto programDetails = engine.expand(degreeToExpand);
        if(programDetails != null) {
            instructionsController.displayProgram(programDetails);
            int programDegree = programDetails.getProgramDegree();
            int maxDegree = engine.getMaxDegreeOfExpand();
            topBarController.updateDegreeLabel(programDegree, maxDegree);
            topBarController.refreshHighlightOptions(programDetails);
            debuggerController.setProgram(programDetails);
        }
    }

    public void btnCollapseListener(int degreeToExpand) {
        ProgramDto programDetails = engine.expand(degreeToExpand);
        if(programDetails != null) {
            instructionsController.displayProgram(programDetails);
            int programDegree = programDetails.getProgramDegree();
            int maxDegree = engine.getMaxDegreeOfExpand();
            topBarController.updateDegreeLabel(programDegree, maxDegree);
            topBarController.refreshHighlightOptions(programDetails);
            debuggerController.setProgram(programDetails);
        }
    }

    public void onHighlightChangedListener(String highlightSelected){
        instructionsController.highlightSelectionOnTable(highlightSelected);
    }

    public void btnStartRegularExecutionListener(long... inputs){
        int degreeOfRun = topBarController.getCurrentDegree();
        ExecutionRunDto runResult= engine.runProgram(degreeOfRun,inputs);
        if(runResult!=null){
            debuggerController.updateRunResult(runResult);
        }
    }

}
