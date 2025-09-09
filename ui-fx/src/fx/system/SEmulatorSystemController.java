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
            ProgramDto programDetails = engine.displayProgram();
            if(programDetails!=null) {
                if(currentProgramName==null) {
                    currentProgramName = programDetails.getName();
                }
                instructionsController.displayProgram(currentProgramName, programDetails);

                int programDegree = ProgramUtil.getDisplayedProgramDegree(currentProgramName, programDetails);
                int maxDegree = ProgramUtil.getDisplayedProgramMaxDegree(currentProgramName, programDetails);
                topBarController.updateDegreeLabel(programDegree, maxDegree);
                topBarController.refreshHighlightOptions(currentProgramName, programDetails);

                //debuggerController.setProgram(programDetails); //todo
                topBarController.setLoadFileText(fileName);
                topBarController.refreshProgramOrFunctionOptions(programDetails);
            }
        }
    }

    public void btnExpandListener(int degreeToExpand) {
        ProgramFunctionDto programFunctionDto = engine.expand(currentProgramName, degreeToExpand);
        if(programFunctionDto != null) {
            instructionsController.displayProgram(currentProgramName, programFunctionDto);
            int programDegree = ProgramUtil.getDisplayedProgramDegree(currentProgramName, programFunctionDto);
            ProgramDto programDetails = engine.displayProgram();
            int maxDegree = ProgramUtil.getDisplayedProgramMaxDegree(currentProgramName, programDetails);
            topBarController.updateDegreeLabel(programDegree, maxDegree);
            topBarController.refreshHighlightOptions(currentProgramName, programFunctionDto);
            //debuggerController.setProgram(programFunctionDto);
        }
    }

    public void btnCollapseListener(int degreeToExpand) {
        ProgramFunctionDto programDto = engine.expand(currentProgramName, degreeToExpand);
        if(programDto != null) {
            instructionsController.displayProgram(currentProgramName, programDto);
            ProgramDto programDetails = engine.displayProgram();
            int programDegree = ProgramUtil.getDisplayedProgramDegree(currentProgramName, programDto);
            int maxDegree = ProgramUtil.getDisplayedProgramMaxDegree(currentProgramName, programDetails);
            topBarController.updateDegreeLabel(programDegree, maxDegree);
            topBarController.refreshHighlightOptions(currentProgramName, programDetails);
            //debuggerController.setProgram(programDetails);
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

    public void onProgramFunctionChangedListener(String ProgramFunctionSelected){
        currentProgramName = ProgramFunctionSelected;
        ProgramDto programDetails = engine.displayProgram();
        instructionsController.displayProgram(currentProgramName,programDetails);
        int programDegree = ProgramUtil.getDisplayedProgramDegree(currentProgramName, programDetails);
        int maxDegree = ProgramUtil.getDisplayedProgramMaxDegree(currentProgramName, programDetails);
        topBarController.updateDegreeLabel(programDegree, maxDegree);
        topBarController.refreshHighlightOptions(currentProgramName, programDetails);

        debuggerController.setProgram(programDetails); //todo
    }


}
