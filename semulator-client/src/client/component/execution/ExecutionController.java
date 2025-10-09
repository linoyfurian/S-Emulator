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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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

    @FXML private TopbarExecutionController topBarExecutionController;
    @FXML private InstructionsController instructionsController;
    @FXML private DebuggerController debuggerController;

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
        this.isProgram = isProgram;

        instructionsController.displayProgram(programDetails);

        int programDegree = ProgramUtil.getDisplayedProgramDegree(programDetails);
        int maxDegree = ProgramUtil.getDisplayedProgramMaxDegree(programDetails);
        topBarExecutionController.updateDegreeLabel(programDegree, maxDegree);

        topBarExecutionController.refreshHighlightOptions(programDetails);
        debuggerController.setProgram(programDetails);
        instructionsController.resetBreakPointSelection();

        topBarExecutionController.setProgramFunctionName(this.programInContext);
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

    public IntegerProperty creditsProperty() { return this.mainController.creditsProperty(); }

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
        if (!isDebugMode) {
            RunProgramRequest runRequest = new RunProgramRequest(programInContext, isProgram, degreeOfExpand, originalInputs, inputs);

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

            HttpClientUtil.postFileAsync(finalUrl, body, new Callback() {
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
                        ExecutionRunDto runResult  = gson.fromJson(json, ExecutionRunDto.class);

                        javafx.application.Platform.runLater(() -> {
                            if(runResult!=null){
                                debuggerController.updateRunResult(runResult);
                                //todo update history
                            }
                            debuggerController.disableChangeOfInput(false);
                        });
                    } finally {
                        response.close();
                    }
                }
            });
        }
        else {
            instructionsController.setIsRunning(true);
//            int breakPointRowIndex = 0;
//            breakPointRowIndex = instructionsController.getBreakPointRowIndex();

            debuggerController.initialStartOfDebugging();
            DebugContextDto initialDebugContext;

            //todo call servlet for initial
//            initialDebugContext = engine.initialStartOfDebugger(degreeOfRun, this.debugContext, originalInputs, inputs);
//            this.debugContext = initialDebugContext;

//            if (breakPointRowIndex != 0) {
//                DebugContextDto debugDetails;
//                debugDetails = engine.breakPointRun(breakPointRowIndex, degreeOfRun, originalInputs, inputs);
//                this.debugContext = debugDetails;
//            }

            DebugProgramRequest debugRequest = new DebugProgramRequest(programInContext, isProgram, degreeOfExpand, this.debugContext, originalInputs, inputs);

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

            HttpClientUtil.postFileAsync(finalUrl, body, new Callback() {
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
                                //todo history

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

}
