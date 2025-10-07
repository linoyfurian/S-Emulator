package client.component.execution;

import client.component.dashboard.DashboardController;
import client.component.execution.debugger.DebuggerController;
import client.component.execution.instructions.InstructionsController;
import client.component.execution.topbar.TopbarExecutionController;
import client.utils.Constants;
import client.utils.display.ProgramUtil;
import client.utils.http.HttpClientUtil;
import com.google.gson.Gson;
import dto.FunctionDto;
import dto.ProgramDto;
import dto.ProgramFunctionDto;
import dto.RunResultDto;
import javafx.application.Platform;
import javafx.fxml.FXML;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

public class ExecutionController {
    private DashboardController mainController;
    private String programInContext;
    private boolean isProgram;


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
        this.instructionsController.highlightByArchitecture(selectedArchitecture);
        return this.instructionsController.checkIfRunIsValid(selectedArchitecture);
    }
}
