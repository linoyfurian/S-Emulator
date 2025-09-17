package fx.system;

import fx.app.util.ProgramUtil;
import fx.component.execution.DebuggerExecutionController;
import fx.component.history.HistoryController;
import fx.component.instructions.InstructionPaneController;
import fx.component.topbar.TopBarController;
import fx.system.load.ProgressDialog;
import jakarta.xml.bind.JAXBException;
import javafx.animation.PauseTransition;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.util.Duration;
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

    public void loadFileAsync(String xmlFile) {
        LoadReport loadReport = null;
        Path path;
        path = Paths.get(xmlFile);

        // Basic input validation
        if (xmlFile == null) return;

        // Build the background task
        Task<LoadReport> task = new Task<>() {
            @Override
            protected LoadReport call() throws Exception {
                updateMessage("Loading…");
                updateProgress(-1, 1); // indeterminate

                // Simulate short delay as required (1.5–2s), still responsive to cancel:
                long ms = 1500;
                long slept = 0;
                while (slept < ms) {
                    if (isCancelled()) {
                        updateMessage("Canceling…");
                        return null;
                    }
                    Thread.sleep(100);
                    slept += 100;
                }

                try {

                    // Heavy work off the JAT:
                    LoadReport lr = engine.loadProgramDetails(path);
                    if (isCancelled()) return null;
                    return lr;
                } catch (JAXBException ex) {
                    // Let JavaFX know it's a failure
                    throw ex;
                }
            }
        };

        // Small progress window
        Stage owner = /* any app stage you have, e.g. primaryStage */ null;
        ProgressDialog progress = new ProgressDialog(owner, "Loading Program");
        progress.bindToTask(task);
        progress.show();

        // On success (back on JAT) – continue your existing UI flow
        task.setOnSucceeded(ev -> {
            try {
                LoadReport lr = task.getValue();
                if (lr == null) { // likely cancelled right at the end
                    progress.close();
                    return;
                }

                if (!lr.isSuccess()) {
                    // Show error inside the progress window briefly
                    String err = (lr.getMessage() != null && !lr.getMessage().isBlank())
                            ? lr.getMessage()
                            : "File failed validation.";
                    progress.setStatus("Error");
                    progress.showDetails(err);
                    progress.setIndeterminate();
                    // Wait ~1.2s then close
                    PauseTransition pt = new PauseTransition(Duration.millis(1800));
                    pt.setOnFinished(e -> progress.close());
                    pt.play();
                    return;
                }

                // Success: fetch the program
                ProgramFunctionDto program = engine.displayProgram();
                if (program == null) {
                    progress.setStatus("Error: Program is empty.");
                    PauseTransition pt = new PauseTransition(Duration.millis(900));
                    pt.setOnFinished(e -> progress.close());
                    pt.play();
                    return;
                }

                progress.setStatus("Loaded successfully.");

                PauseTransition pt = new PauseTransition(Duration.millis(600));
                pt.setOnFinished(e2 -> progress.close());
                pt.play();

//                // Close the progress before updating UI
//                progress.close();

                //update in UI
                if (currentProgramName == null) {
                    currentProgramName = program.getName();
                }

                instructionsController.displayProgram(program);

                int programDegree = ProgramUtil.getDisplayedProgramDegree(program);
                int maxDegree = ProgramUtil.getDisplayedProgramMaxDegree(program);
                topBarController.updateDegreeLabel(programDegree, maxDegree);

                topBarController.refreshHighlightOptions(program);

                debuggerController.setProgram(program);
                topBarController.setLoadFileText(path.toString());

                List<String> programOrFunctionOptions = engine.getProgramOrFunctionNames();
                topBarController.refreshProgramOrFunctionOptions(programOrFunctionOptions);

                // NOTE: “Only one file loaded at a time” happens naturally:
                // a successful load replaces the previous program in the engine/UI.

            } catch (Exception ex) {
                progress.close();
            }
        });

        // On failure (exception thrown in call)
        task.setOnFailed(ev -> {
            Throwable ex = task.getException();
            String msg = (ex != null && ex.getMessage() != null) ? ex.getMessage() : "Unknown load error.";
            progress.setStatus("Error: " + msg);
            progress.setIndeterminate();
            PauseTransition pt = new PauseTransition(Duration.millis(1200));
            pt.setOnFinished(e -> progress.close());
            pt.play();
        });

        // On cancel
        task.setOnCancelled(ev -> {
            progress.setStatus("Canceled.");
            progress.setIndeterminate();
            PauseTransition pt = new PauseTransition(Duration.millis(600));
            pt.setOnFinished(e -> progress.close());
            pt.play();
        });

        Thread t = new Thread(task, "load-xml-task");
        t.setDaemon(true);
        t.start();
    }

}