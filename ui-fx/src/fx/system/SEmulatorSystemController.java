package fx.system;

import fx.app.util.ProgramUtil;
import fx.component.execution.DebuggerExecutionController;
import fx.component.history.HistoryController;
import fx.component.instructions.InstructionPaneController;
import fx.component.topbar.TopBarController;
import jakarta.xml.bind.JAXBException;
import javafx.fxml.FXML;
import semulator.api.LoadReport;
import semulator.api.dto.*;
import semulator.core.SEmulatorEngine;
import semulator.core.SEmulatorEngineImpl;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class SEmulatorSystemController {

    private SEmulatorEngine engine = new SEmulatorEngineImpl();
    @FXML private TopBarController topBarController;
    @FXML private InstructionPaneController instructionsController;
    @FXML private DebuggerExecutionController debuggerController;
    @FXML private HistoryController historyController;

    private String currentProgramName;
    DebugContextDto debugContext = null;

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

    public void btnRunListener(boolean isDebugMode, Map<String, Long> originalInputs, long ... inputs){
        instructionsController.highlightLine(-1);

        int degreeOfRun = topBarController.getCurrentDegree();
        if(!isDebugMode) {
            ExecutionRunDto runResult= engine.runProgram(degreeOfRun, originalInputs, inputs);
            if(runResult!=null){
                debuggerController.updateRunResult(runResult);
                List<RunResultDto> programInContextRunHistory = engine.getProgramInContextRunHistory();
                historyController.updateHistoryRunTable(programInContextRunHistory);
            }
        }
        else {
            debuggerController.initialStartOfDebugging();
            DebugContextDto debugDetails = engine.debugProgram(degreeOfRun,this.debugContext, originalInputs, inputs);
            this.debugContext = debugDetails;
            debuggerController.updateDebugResult(this.debugContext);
            long nextInstructionNumber = debugContext.getNextInstructionNumber();
            long currInstructionToHighlight = debugContext.getPreviousInstructionNumber();
            instructionsController.highlightLine((int)currInstructionToHighlight - 1);
            if(nextInstructionNumber == 0) {
                engine.addCurrentRunToHistory(this.debugContext, degreeOfRun);
                List<RunResultDto> programInContextRunHistory = engine.getProgramInContextRunHistory();
                historyController.updateHistoryRunTable(programInContextRunHistory);
            }

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
        List<RunResultDto> programInContextRunHistory = engine.getProgramInContextRunHistory();
        historyController.updateHistoryRunTable(programInContextRunHistory);

        instructionsController.highlightLine(-1);

    }

    public void cleanDebugContext(){
        debugContext = null;
    }

    public void btnStepOverListener(){
        int degreeOfRun = topBarController.getCurrentDegree();
        DebugContextDto debugDetails = engine.debugProgram(degreeOfRun, this.debugContext, this.debugContext.getOriginalInputs());
        this.debugContext = debugDetails;
        long nextInstructionNumber = debugContext.getNextInstructionNumber();
        if(nextInstructionNumber == 0){
            engine.addCurrentRunToHistory(this.debugContext, degreeOfRun);
            List<RunResultDto> programInContextRunHistory = engine.getProgramInContextRunHistory();
            historyController.updateHistoryRunTable(programInContextRunHistory);

        }
        long currInstructionToHighlight = debugContext.getPreviousInstructionNumber();
        instructionsController.highlightLine((int)currInstructionToHighlight - 1);
        debuggerController.updateDebugResult(this.debugContext);
    }

    public void btnStepBackListener(){
        if(this.debugContext!=null){
            long presInstructionNumber = this.debugContext.getPreviousInstructionNumber();
            if(presInstructionNumber==1){
                debuggerController.updateDebugPrevResult(this.debugContext);
                debuggerController.disableStepBackBtn();
                instructionsController.highlightLine(0); //todo: new
            }
            else{
                this.debugContext = this.debugContext.getPrevDebugContext();
                debuggerController.updateDebugResult(this.debugContext);
                long currInstructionToHighlight = debugContext.getPreviousInstructionNumber();
                instructionsController.highlightLine((int)currInstructionToHighlight - 1);
            }



        }
    }

    public void btnStopListener(){
        int degreeOfRun = topBarController.getCurrentDegree();
        engine.addCurrentRunToHistory(this.debugContext, degreeOfRun);
        List<RunResultDto> programInContextRunHistory = engine.getProgramInContextRunHistory();
        historyController.updateHistoryRunTable(programInContextRunHistory);

        instructionsController.highlightLine(-1);
    }

    public void onReRunButtonListener(RunResultDto selectedRun){
        int currentDegree = topBarController.getCurrentDegree();
        int degreeOfRun = selectedRun.getDegreeOfRun();
        updateSystemToTheDesiredDegree(currentDegree, degreeOfRun);
        debuggerController.initialOfNewRun();
        Map<String,Long> inputs = selectedRun.getInputs();
        debuggerController.applyRelevantInputs(inputs);

        instructionsController.highlightLine(-1);
    }

    private void updateSystemToTheDesiredDegree(int currentDegree, int degreeOfRun){
        if(degreeOfRun==currentDegree){
            return;
        }
        else if(degreeOfRun<currentDegree){
            btnCollapseListener(degreeOfRun);
        }
        else
            btnExpandListener(degreeOfRun);
    }

    public void onBtnResumeListener(){
        int degreeOfRun = topBarController.getCurrentDegree();
        DebugContextDto debugDetails = engine.resumeProgram(degreeOfRun, this.debugContext, this.debugContext.getOriginalInputs());
        this.debugContext = debugDetails;
        long nextInstructionNumber = debugContext.getNextInstructionNumber();
        if(nextInstructionNumber == 0){
            engine.addCurrentRunToHistory(this.debugContext, degreeOfRun);
            List<RunResultDto> programInContextRunHistory = engine.getProgramInContextRunHistory();
            historyController.updateHistoryRunTable(programInContextRunHistory);
        }
        debuggerController.updateDebugResult(this.debugContext);

        instructionsController.highlightLine(-1);
    }

    public void btnNewRunListener(){
        instructionsController.highlightLine(-1);
    }
}
