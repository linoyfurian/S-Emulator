package fx.system;

import fx.app.display.Theme;
import fx.app.util.ProgramUtil;
import fx.component.execution.DebuggerExecutionController;
import fx.component.history.HistoryController;
import fx.component.instructions.InstructionPaneController;
import fx.component.topbar.TopBarController;
import fx.system.create.AddProgramController;
import javafx.stage.FileChooser;
import semulator.api.dto.ProgramDraft;
import fx.system.load.ProgressDialog;
import jakarta.xml.bind.JAXBException;
import javafx.animation.PauseTransition;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import semulator.api.LoadReport;
import semulator.api.dto.*;
import semulator.core.SEmulatorEngine;
import semulator.core.SEmulatorEngineImpl;

import java.io.File;
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


    @FXML private ScrollPane systemScrollPane;

    private Theme currTheme = Theme.Default;
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

        instructionsController.resetBreakPointSelection();
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

        instructionsController.resetBreakPointSelection();
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
            instructionsController.setIsRunning(true);
            int breakPointRowIndex = 0;
            breakPointRowIndex = instructionsController.getBreakPointRowIndex();

            debuggerController.initialStartOfDebugging();
            DebugContextDto initialDebugContext;

            initialDebugContext = engine.initialStartOfDebugger(degreeOfRun, this.debugContext, originalInputs, inputs);
            this.debugContext = initialDebugContext;

            if(breakPointRowIndex != 0){
                DebugContextDto debugDetails;
                debugDetails = engine.breakPointRun(breakPointRowIndex, degreeOfRun, originalInputs, inputs);
                this.debugContext = debugDetails;

                /**option of saving history, costs a lot of time*/
                /*
                long instructionToDebug = 1;
                while(instructionToDebug != breakPointRowIndex && instructionToDebug!=0){
                    debugDetails = engine.debugProgram(degreeOfRun, this.debugContext, originalInputs, inputs);
                    this.debugContext = debugDetails;
                    instructionToDebug = debugDetails.getNextInstructionNumber();
                }

                 */
            }

            long currInstructionToHighlight = 1;
            currInstructionToHighlight = this.debugContext.getNextInstructionNumber();
            instructionsController.highlightLine((int)(currInstructionToHighlight-1));
            debuggerController.updateDebugResult(this.debugContext);

            if(currInstructionToHighlight == 0) {
                instructionsController.setIsRunning(false);
                engine.addCurrentRunToHistory(this.debugContext, degreeOfRun);
                List<RunResultDto> programInContextRunHistory = engine.getProgramInContextRunHistory();
                historyController.updateHistoryRunTable(programInContextRunHistory);
            }
        }
    }

    public void onProgramFunctionChangedListener(String programFunctionSelected){
        instructionsController.setIsRunning(false);
        instructionsController.resetBreakPointSelection();

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

        long prevInstructionNumber = debugContext.getPreviousInstructionNumber();
        String variableToHighLight = this.instructionsController.getInstructionsMainVariable(prevInstructionNumber);
        debuggerController.updateVariableHighlight(variableToHighLight);

        long currInstructionToHighlight = debugContext.getNextInstructionNumber();
        if(currInstructionToHighlight == 0){
            instructionsController.setIsRunning(false);
            engine.addCurrentRunToHistory(this.debugContext, degreeOfRun);
            List<RunResultDto> programInContextRunHistory = engine.getProgramInContextRunHistory();
            historyController.updateHistoryRunTable(programInContextRunHistory);
        }
        else
            instructionsController.highlightLine((int)currInstructionToHighlight - 1);
        debuggerController.updateDebugResult(this.debugContext);

    }

    public void btnStepBackListener(){
        if(this.debugContext!=null){
            this.debugContext = this.debugContext.getPrevDebugContext();
            long currInstructionToHighlight = debugContext.getNextInstructionNumber();
            instructionsController.highlightLine((int)currInstructionToHighlight - 1);
            debuggerController.updateDebugResult(this.debugContext);

            if(currInstructionToHighlight==1)
                debuggerController.disableStepBackBtn();

            debuggerController.updateVariableHighlight("");

            if(this.debugContext.getPrevDebugContext().getPrevDebugContext()==null)
                debuggerController.disableStepBackBtn();
        }

    }

    public void btnStopListener(){
        int degreeOfRun = topBarController.getCurrentDegree();
        engine.addCurrentRunToHistory(this.debugContext, degreeOfRun);
        List<RunResultDto> programInContextRunHistory = engine.getProgramInContextRunHistory();
        historyController.updateHistoryRunTable(programInContextRunHistory);

        instructionsController.setIsRunning(false);
        instructionsController.highlightLine(-1);
        debuggerController.updateVariableHighlight("");
    }

    public void onReRunButtonListener(RunResultDto selectedRun){
        instructionsController.setIsRunning(false);
        instructionsController.resetBreakPointSelection();

        int currentDegree = topBarController.getCurrentDegree();
        int degreeOfRun = selectedRun.getDegreeOfRun();
        updateSystemToTheDesiredDegree(currentDegree, degreeOfRun);
        debuggerController.initialOfNewRun();
        Map<String,Long> inputs = selectedRun.getInputs();
        debuggerController.applyRelevantInputs(inputs);

        instructionsController.highlightLine(-1);
        debuggerController.updateVariableHighlight("");
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
        instructionsController.setIsRunning(false);
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
        debuggerController.updateVariableHighlight("");
        instructionsController.highlightLine(-1);
    }

    public void btnNewRunListener(){
        instructionsController.setIsRunning(false);
        instructionsController.highlightLine(-1);
        debuggerController.updateVariableHighlight("");
        instructionsController.resetBreakPointSelection();
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
                long ms = 1000;
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
                    PauseTransition pt = new PauseTransition(Duration.millis(1200));
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

                PauseTransition pt = new PauseTransition(Duration.millis(500));
                pt.setOnFinished(e2 -> progress.close());
                pt.play();

                // Close the progress before updating UI
                progress.close();

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

                instructionsController.resetBreakPointSelection();

                List<RunResultDto> programInContextRunHistory = engine.getProgramInContextRunHistory();
                historyController.updateHistoryRunTable(programInContextRunHistory);

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

    public void onStyleSheetChangedListener(Theme selectedStyleSheet) {
        instructionsController.onStyleSheetChangedListener(selectedStyleSheet);
        debuggerController.onStyleSheetChangedListener(selectedStyleSheet);
        historyController.onStyleSheetChangedListener(selectedStyleSheet);

        systemScrollPane.getStylesheets().clear();
        switch(selectedStyleSheet) {
            case Theme.Default:
                systemScrollPane.getStylesheets().add("/fx/system/semulatorSystem.css");
                currTheme = Theme.Default;
                break;
            case Theme.Dark:
                systemScrollPane.getStylesheets().add("/fx/system/semulatorSystemV2.css");
                currTheme = Theme.Dark;
                break;
            case Theme.Ocean:
                systemScrollPane.getStylesheets().add("/fx/system/semulatorSystemV3.css");
                currTheme = Theme.Ocean;
                break;
        }
    }

    public void btnCreateAProgramListener() throws Exception{
        final String FXML_PATH = "/fx/system/create/addProgram.fxml";

        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH));
        ScrollPane root = loader.load();

        AddProgramController controller = loader.getController();
        controller.setMainController(this);

        Stage dialog = new Stage();
        dialog.setTitle("Create Program");
        dialog.initModality(Modality.WINDOW_MODAL);

        root.getStylesheets().clear();
        switch(currTheme) {
            case Theme.Default:
                root.getStylesheets().add("/fx/system/create/addProgram.css");
                break;
            case Theme.Dark:
                root.getStylesheets().add("/fx/system/create/addProgramV2.css");
                break;
            case Theme.Ocean:
                root.getStylesheets().add("/fx/system/create/addProgramV3.css");
                break;
        }

        Scene scene = new Scene(root);
        dialog.setScene(scene);
        dialog.setResizable(false);
        dialog.show();
    }

    public void onBtnUploadProgramListenter(ProgramDraft newProgram){
        if(newProgram == null){
            return;
        }
        engine.uploadCreatedProgram(newProgram);

        ProgramFunctionDto program = engine.displayProgram();
        instructionsController.displayProgram(program);

        int programDegree = ProgramUtil.getDisplayedProgramDegree(program);
        int maxDegree = ProgramUtil.getDisplayedProgramMaxDegree(program);
        topBarController.updateDegreeLabel(programDegree, maxDegree);

        topBarController.refreshHighlightOptions(program);

        debuggerController.setProgram(program);

        List<String> programOrFunctionOptions = engine.getProgramOrFunctionNames();
        topBarController.refreshProgramOrFunctionOptions(programOrFunctionOptions);

        List<RunResultDto> programInContextRunHistory = engine.getProgramInContextRunHistory();
        historyController.updateHistoryRunTable(programInContextRunHistory);
    }

    public void saveProgramToFile(ProgramDraft newProgram, File fileToSave){
        engine.saveCreatedProgramToFile(newProgram, fileToSave);
    }

}