package client.component.execution;

import client.component.dashboard.DashboardController;
import client.component.execution.debugger.DebuggerController;
import client.component.execution.instructions.InstructionsController;
import client.component.execution.topbar.TopbarExecutionController;
import client.utils.Constants;
import client.utils.architecture.ArchitectureType;
import client.utils.display.ProgramUtil;
import client.utils.http.HttpClientUtil;
import com.google.gson.Gson;
import dto.*;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ExecutionController {
    private DashboardController mainController;
    private String programInContext;
    private boolean isProgram;
    private double averageCredits;

    private DebugContextDto debugContext = null;

    @FXML private ScrollPane systemScrollPane;

    @FXML private TopbarExecutionController topBarExecutionController;
    @FXML private InstructionsController instructionsController;
    @FXML private DebuggerController debuggerController;

    private Stage showRunningDialog() {
        Stage owner = (Stage) systemScrollPane.getScene().getWindow();

        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setPrefSize(70, 70);

        Label label = new Label("Running program…");

        VBox root = new VBox(12, spinner, label);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(16));

        Stage dialog = new Stage(StageStyle.UTILITY);
        dialog.setTitle("Please wait");
        dialog.initOwner(owner);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setResizable(false);
        dialog.setScene(new Scene(root, 260, 140));
        dialog.show();

        return dialog;
    }

    private Stage showResumeDialog() {
        Stage owner = (Stage) systemScrollPane.getScene().getWindow();

        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setPrefSize(70, 70);

        Label label = new Label("Resume program…");

        VBox root = new VBox(12, spinner, label);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(16));

        Stage dialog = new Stage(StageStyle.UTILITY);
        dialog.setTitle("Please wait");
        dialog.initOwner(owner);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setResizable(false);
        dialog.setScene(new Scene(root, 260, 140));
        dialog.show();

        return dialog;
    }

    @FXML
    public void initialize() {
        if (topBarExecutionController != null) {
            topBarExecutionController.setMainController(this);
        }
        if (instructionsController != null) {
            instructionsController.setMainController(this);
        }
        if (debuggerController != null) {
            debuggerController.setMainController(this);
        }
    }

    public void setProgramInContext(String programInContext) {
        this.programInContext = programInContext;
    }
    public void setIsProgram(boolean isProgram) {
        this.isProgram = isProgram;
    }

    public void setMainController(DashboardController mainController) {
        this.mainController = mainController;
        this.topBarExecutionController.bindCredits();
    }

    public void setAverageCredits(double averageCredits) {
        this.averageCredits = averageCredits;
    }

    public void setUserName(String userName) {
        this.topBarExecutionController.setUserName(userName);
    }

    public void initialProgramDetails(ProgramFunctionDto programDetails, boolean isProgram) {
        this.programInContext = programDetails.getName();
        String displayName;
        if (isProgram) {
            this.programInContext = programDetails.getName();
            displayName = programDetails.getName();
        }
        else{
            FunctionDto functionDto = (FunctionDto) programDetails;
            displayName = functionDto.getUserString();
        }


        this.isProgram = isProgram;

        instructionsController.displayProgram(programDetails);

        int programDegree = ProgramUtil.getDisplayedProgramDegree(programDetails);
        int maxDegree = ProgramUtil.getDisplayedProgramMaxDegree(programDetails);
        topBarExecutionController.updateDegreeLabel(programDegree, maxDegree);

        topBarExecutionController.refreshHighlightOptions(programDetails);
        debuggerController.setProgram(programDetails);
       // instructionsController.resetBreakPointSelection();

        topBarExecutionController.setProgramFunctionName(displayName);
    }

    public void onHighlightChangedListener(String highlightSelected){
        instructionsController.highlightSelectionOnTable(highlightSelected);
    }

    public void btnExpandListener(int degreeToExpand) {
        ProgramFunctionDto result = null;
        String finalUrl = HttpUrl
                .parse(Constants.EXPAND_SERVLET)
                .newBuilder()
                .addQueryParameter("program_name", this.programInContext)
                .addQueryParameter("is_program", String.valueOf(isProgram))
                .addQueryParameter("degree_of_expand", String.valueOf(degreeToExpand))
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    if (!response.isSuccessful() || response.body() == null) {
                        return;
                    }

                    String json = response.body().string();

                    Gson gson = new Gson();
                    ProgramFunctionDto expandedProgramDetails;

                    if (isProgram) {
                        // ProgramDto implements ProgramFunctionDto
                        ProgramDto programDto = gson.fromJson(json, ProgramDto.class);
                        expandedProgramDetails = programDto;
                    } else {
                        // FunctionDto implements ProgramFunctionDto
                        FunctionDto functionDto = gson.fromJson(json, FunctionDto.class);
                        expandedProgramDetails = functionDto;
                    }
                    javafx.application.Platform.runLater(() -> {
                        instructionsController.clearAllHighlightedInstructions();
                        instructionsController.displayProgram(expandedProgramDetails);
                        topBarExecutionController.refreshHighlightOptions(expandedProgramDetails);
                        topBarExecutionController.updateCurrentDegreeLabel(degreeToExpand);
                        debuggerController.setProgram(expandedProgramDetails);
                        debuggerController.updateRunBtnDisable();

                    });
                } finally {
                    response.close();
                }
            }
        });
    }

    public boolean checkIfRunIsValid(String selectedArchitecture){
        boolean result;
        this.instructionsController.highlightByArchitecture(selectedArchitecture);
        double currentCredits = this.topBarExecutionController.getCredits();
        result = this.instructionsController.checkIfRunIsValid(selectedArchitecture) && isEnoughCreditsAccordingToAverage(currentCredits, selectedArchitecture);
        return result;
    }

    public LongProperty creditsProperty() { return this.mainController.creditsProperty(); }

    private boolean isEnoughCreditsAccordingToAverage(double credits, String selectedArchitecture) {
        ArchitectureType architecture = ProgramUtil.getArchitecture(selectedArchitecture);
        double predictedCredits = 0;
        predictedCredits = architecture.getRunCost() + averageCredits;
        if (predictedCredits <= credits)
            return true;
        else
            return false;
    }

    public void onRunBtnListener(boolean isDebugMode, Map<String, Long> originalInputs, long[] inputs) {
        int degreeOfExpand = this.topBarExecutionController.getCurrentDegree();
        Gson gson = new Gson();
        String architecture = this.debuggerController.getArchitecture();
        long currentCredits = this.topBarExecutionController.getCredits();
        long newCredits = currentCredits - ProgramUtil.getCost(architecture);
        updateUsedCredits(ProgramUtil.getCost(architecture));
        this.mainController.setCredits(newCredits);

        if (!isDebugMode) {
            RunProgramRequest runRequest = new RunProgramRequest(newCredits, programInContext, architecture, isProgram, degreeOfExpand, originalInputs, inputs);

            // Convert to JSON
            String json = gson.toJson(runRequest);

            RequestBody body = RequestBody.create(
                    json, MediaType.get("application/json; charset=utf-8")
            );

            String finalUrl = HttpUrl
                    .parse(Constants.REGULAR_RUN_SERVLET)
                    .newBuilder()
                    .build()
                    .toString();

            Stage runningDialog = showRunningDialog();

            HttpClientUtil.postAsync(finalUrl, body, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    try {
                        if (!response.isSuccessful() || response.body() == null) {
                            javafx.application.Platform.runLater(() -> {
                                if (runningDialog.isShowing()) runningDialog.close();
                            });
                            return;
                        }
                        String json = response.body().string();
                        Gson gson = new Gson();
                        ExecutionRunDto runResult  = gson.fromJson(json, ExecutionRunDto.class);

                        javafx.application.Platform.runLater(() -> {
                            if (runningDialog.isShowing()) runningDialog.close();
                            if(runResult!=null){
                                debuggerController.updateRunResult(runResult);
                                mainController.updateHistory();
                                mainController.updateRunsNumber();
                                long currentCredits = topBarExecutionController.getCredits();
                                long newCredits = currentCredits - runResult.getCycles();
                                mainController.setCredits(newCredits);
                                updateUsedCredits(runResult.getCycles());
                                if(!runResult.isRunSuccess()){
                                    //NOT ENOUGH CREDITS
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Insufficient Credits");
                                    alert.setHeaderText("Execution stopped");
                                    alert.setContentText("You don’t have enough credits to complete this execution, so it was stopped.");

                                    Stage stage = (Stage) systemScrollPane.getScene().getWindow();
                                    alert.initOwner(stage);
                                    alert.initModality(Modality.WINDOW_MODAL);
                                    alert.showAndWait();
                                }
                            }
                            debuggerController.disableChangeOfInput(false);
                            debuggerController.updateRunBtnDisable();
                        });
                    } finally {
                        response.close();
                    }
                }
            });
        }
        else {
            topBarExecutionController.initialDebugMode();
            instructionsController.setIsRunning(true);
//            int breakPointRowIndex = 0;
//            breakPointRowIndex = instructionsController.getBreakPointRowIndex();

            debuggerController.initialStartOfDebugging();

//            if (breakPointRowIndex != 0) {
//                DebugContextDto debugDetails;
//                debugDetails = engine.breakPointRun(breakPointRowIndex, degreeOfRun, originalInputs, inputs);
//                this.debugContext = debugDetails;
//            }

            DebugProgramRequest debugRequest = new DebugProgramRequest(newCredits, programInContext, isProgram, degreeOfExpand, this.debugContext, originalInputs, inputs);

            // Convert to JSON
            String json = gson.toJson(debugRequest);

            RequestBody body = RequestBody.create(
                    json, MediaType.get("application/json; charset=utf-8")
            );

            String finalUrl = HttpUrl
                    .parse(Constants.DEBUG_RUN_SERVLET)
                    .newBuilder()
                    .addQueryParameter("is_initial_debug", "true")
                    .build()
                    .toString();

            HttpClientUtil.postAsync(finalUrl, body, new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {

                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    try {
                        if (!response.isSuccessful() || response.body() == null) {
                            return;
                        }

                        String json = response.body().string();

                        Gson gson = new Gson();
                        DebugContextDto debugResult  = gson.fromJson(json, DebugContextDto.class);

                        debugContext = debugResult;
                        javafx.application.Platform.runLater(() -> {
                            long currInstructionToHighlight = 1;
                            currInstructionToHighlight = debugContext.getNextInstructionNumber();
                            instructionsController.highlightLine((int) (currInstructionToHighlight - 1));
                            debuggerController.updateDebugResult(debugContext);

                            if (currInstructionToHighlight == 0) {
                                instructionsController.setIsRunning(false);
                                debuggerController.disableChangeOfInput(false);
                            }
                        });
                    } finally {
                        response.close();
                    }
                }
            });

        }

    }

    public void cleanDebugContext(){
        debugContext = null;
    }


    public void btnStepOverListener(){
        int degreeOfRun = topBarExecutionController.getCurrentDegree();
        long currentCredits = this.topBarExecutionController.getCredits();
        DebugProgramRequest debugRequest = new DebugProgramRequest(currentCredits, programInContext, isProgram, degreeOfRun, this.debugContext, this.debugContext.getOriginalInputs(), null);

        Gson gson = new Gson();
        // Convert to JSON
        String json = gson.toJson(debugRequest);

        RequestBody body = RequestBody.create(
                json, MediaType.get("application/json; charset=utf-8")
        );

        String finalUrl = HttpUrl
                .parse(Constants.DEBUG_RUN_SERVLET)
                .newBuilder()
                .addQueryParameter("is_initial_debug", "false")
                .build()
                .toString();

        HttpClientUtil.postAsync(finalUrl, body, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    if (!response.isSuccessful() || response.body() == null) {
                        return;
                    }

                    String json = response.body().string();

                    Gson gson = new Gson();
                    DebugContextDto debugResult  = gson.fromJson(json, DebugContextDto.class);
                    debugContext = debugResult;
                    boolean isEnoughCredits = debugContext.isSuccess();
                    javafx.application.Platform.runLater(() -> {
                        if(!isEnoughCredits){
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Insufficient Credits");
                            alert.setHeaderText("Debug stopped");
                            alert.setContentText("You don’t have enough credits to complete this execution, so it was stopped.");

                            Stage stage = (Stage) systemScrollPane.getScene().getWindow();
                            alert.initOwner(stage);
                            alert.initModality(Modality.WINDOW_MODAL);
                            alert.showAndWait();
                        }
                        else{
                            long prevInstructionNumber = debugContext.getPreviousInstructionNumber();
                            String variableToHighLight = instructionsController.getInstructionsMainVariable(prevInstructionNumber);
                            debuggerController.updateVariableHighlight(variableToHighLight);
                            long currentCredits = topBarExecutionController.getCredits();
                            long newCredits = currentCredits - debugContext.getCurrentInstructionsCycles();
                            mainController.setCredits(newCredits);
                            updateUsedCredits(debugContext.getCurrentInstructionsCycles());
                        }

                        long currInstructionToHighlight = debugContext.getNextInstructionNumber();
                        if(currInstructionToHighlight == 0 || (!isEnoughCredits)){
                            debuggerController.finishDebug();
                            instructionsController.setIsRunning(false);
                            topBarExecutionController.endDebugMode();
                            String architecture = debuggerController.getArchitecture();
                            int degreeOfRun = topBarExecutionController.getCurrentDegree();
                            addCurrentRunToHistory(debugContext, degreeOfRun, architecture);
                            mainController.updateRunsNumber();
                            debuggerController.disableChangeOfInput(false);
                            debuggerController.updateRunBtnDisable();
                        }
                        else
                            instructionsController.highlightLine((int)currInstructionToHighlight - 1);
                        debuggerController.updateDebugResult(debugContext);
                    });
                } finally {
                    response.close();
                }
            }
        });

    }

    public void addCurrentRunToHistory(DebugContextDto debugContext, int degreeOfRun, String architecture){
        HistoryRequestDto historyRequest = new HistoryRequestDto(debugContext,degreeOfRun,this.isProgram, this.programInContext,architecture);
        Gson gson = new Gson();
        // Convert to JSON
        String json = gson.toJson(historyRequest);

        RequestBody body = RequestBody.create(
                json, MediaType.get("application/json; charset=utf-8")
        );

        String finalUrl = HttpUrl
                .parse(Constants.HISTORY_RUN_SERVLET)
                .newBuilder()
                .build()
                .toString();

        HttpClientUtil.postAsync(finalUrl, body, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    if (!response.isSuccessful() || response.body() == null) {
                        return;
                    }
                    mainController.updateHistory();
                }
                catch (Exception e) {
                }
            }
        });
    }

    public void btnStopListener(){
        int degreeOfRun = topBarExecutionController.getCurrentDegree();

        addCurrentRunToHistory(debugContext,degreeOfRun, debuggerController.getArchitecture());

        instructionsController.setIsRunning(false);
        instructionsController.highlightLine(-1);
        debuggerController.updateVariableHighlight("");
        debuggerController.disableChangeOfInput(false);
        debuggerController.updateRunBtnDisable();
    }

    public void onBtnResumeListener(){
        instructionsController.setIsRunning(false);
        int degreeOfRun = topBarExecutionController.getCurrentDegree();
        long credits = topBarExecutionController.getCredits();

        DebugProgramRequest debugRequest = new DebugProgramRequest(credits, programInContext, isProgram, degreeOfRun, this.debugContext, this.debugContext.getOriginalInputs(), null);

        Gson gson = new Gson();
        // Convert to JSON
        String json = gson.toJson(debugRequest);

        RequestBody body = RequestBody.create(
                json, MediaType.get("application/json; charset=utf-8")
        );

        String finalUrl = HttpUrl
                .parse(Constants.DEBUG_RUN_SERVLET)
                .newBuilder()
                .addQueryParameter("operation", "resume")
                .build()
                .toString();

        Stage resumeDialog = showResumeDialog();
        HttpClientUtil.postAsync(finalUrl, body, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    if (!response.isSuccessful() || response.body() == null) {
                        javafx.application.Platform.runLater(() -> {
                            if (resumeDialog.isShowing()) resumeDialog.close();
                        });
                        return;
                    }

                    String json = response.body().string();

                    Gson gson = new Gson();
                    DebugContextDto debugResult  = gson.fromJson(json, DebugContextDto.class);

                    debugContext = debugResult;

                    boolean isEnoughCredits = debugContext.isSuccess();
                    javafx.application.Platform.runLater(() -> {
                        if (resumeDialog.isShowing()) resumeDialog.close();
                        if(!isEnoughCredits){
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Insufficient Credits");
                            alert.setHeaderText("Resume stopped");
                            alert.setContentText("You don’t have enough credits to complete this execution, so it was stopped.");

                            Stage stage = (Stage) systemScrollPane.getScene().getWindow();
                            alert.initOwner(stage);
                            alert.initModality(Modality.WINDOW_MODAL);
                            alert.showAndWait();
                        }

                        long currentCredits = topBarExecutionController.getCredits();
                        long newCredits = currentCredits - debugContext.getCurrentInstructionsCycles();
                        mainController.setCredits(newCredits);
                        updateUsedCredits(debugContext.getCurrentInstructionsCycles());

                        long prevInstructionNumber = debugContext.getPreviousInstructionNumber();
                        String variableToHighLight = instructionsController.getInstructionsMainVariable(prevInstructionNumber);
                        debuggerController.updateVariableHighlight(variableToHighLight);

                        long currInstructionToHighlight = debugContext.getNextInstructionNumber();
                     //   instructionsController.highlightLine((int)prevInstructionNumber - 1);
                        if(currInstructionToHighlight == 0){
                            instructionsController.setIsRunning(false);
                            topBarExecutionController.endDebugMode();

                            String architecture = debuggerController.getArchitecture();
                            int degreeOfRun = topBarExecutionController.getCurrentDegree();
                            addCurrentRunToHistory(debugContext, degreeOfRun, architecture);
                            mainController.updateRunsNumber();
                            debuggerController.disableChangeOfInput(false);
                        }
                        else
                            instructionsController.highlightLine((int)currInstructionToHighlight - 1);
                        debuggerController.updateDebugResult(debugContext);
                        debuggerController.updateRunBtnDisable();
                    });
                } finally {
                    response.close();
                }
            }
        });
    }

    public void btnNewRunListener(){
        instructionsController.setIsRunning(false);
        instructionsController.highlightLine(-1);
        debuggerController.updateVariableHighlight("");
        instructionsController.resetBreakPointSelection();

        debuggerController.disableChangeOfInput(false);
        debuggerController.updateRunBtnDisable();
    }


    public void updateUsedCredits(int credits){
        String finalUrl = HttpUrl
                .parse(Constants.CREDITS_SERVLET)
                .newBuilder()
                .addQueryParameter("operation", "used")
                .addQueryParameter("credits",String.valueOf(credits))
                .build()
                .toString();

        RequestBody body = RequestBody.create("", MediaType.parse("text/plain"));

        HttpClientUtil.postAsync(finalUrl, body, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    if (!response.isSuccessful() || response.body() == null) {
                        return;
                    }
                } finally {
                    response.close();
                }
            }
        });
    }

    public void initialReRun(RunResultDto selectedRun){
        instructionsController.setIsRunning(false);
        int degreeOfRun = selectedRun.getDegreeOfRun();
        ProgramFunctionDto result = null;
        String finalUrl = HttpUrl
                .parse(Constants.EXPAND_SERVLET)
                .newBuilder()
                .addQueryParameter("program_name", this.programInContext)
                .addQueryParameter("is_program", String.valueOf(isProgram))
                .addQueryParameter("degree_of_expand", String.valueOf(degreeOfRun))
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                try {
                    if (!response.isSuccessful() || response.body() == null) {
                        return;
                    }

                    String json = response.body().string();

                    Gson gson = new Gson();
                    ProgramFunctionDto expandedProgramDetails;

                    String displayName;
                    if (isProgram) {
                        // ProgramDto implements ProgramFunctionDto
                        ProgramDto programDto = gson.fromJson(json, ProgramDto.class);
                        expandedProgramDetails = programDto;
                        displayName = expandedProgramDetails.getName();
                    } else {
                        // FunctionDto implements ProgramFunctionDto
                        FunctionDto functionDto = gson.fromJson(json, FunctionDto.class);
                        expandedProgramDetails = functionDto;
                        displayName = functionDto.getUserString();
                    }
                    javafx.application.Platform.runLater(() -> {
                        instructionsController.clearAllHighlightedInstructions();
                        instructionsController.displayProgram(expandedProgramDetails);
                        topBarExecutionController.refreshHighlightOptions(expandedProgramDetails);
                        topBarExecutionController.updateCurrentDegreeLabel(degreeOfRun);
                        debuggerController.setProgram(expandedProgramDetails);
                        debuggerController.updateRunBtnDisable();

                        topBarExecutionController.setProgramFunctionName(displayName);
                        debuggerController.initialOfNewRun();
                        Map<String,Long> inputs = selectedRun.getInputs();
                        debuggerController.applyRelevantInputs(inputs);
                        instructionsController.highlightLine(-1);
                        debuggerController.updateVariableHighlight("");
                        debuggerController.disableChangeOfInput(false);
                        debuggerController.updateArchitecture(selectedRun.getArchitecture());
                    });
                } finally {
                    response.close();
                }
            }
        });

    }

    private void updateSystemToTheDesiredDegree(int currentDegree, int degreeOfRun){

    }
}