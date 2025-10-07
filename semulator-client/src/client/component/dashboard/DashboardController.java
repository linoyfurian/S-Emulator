package client.component.dashboard;

import client.component.dashboard.programsFunctions.ProgramsFunctionsController;
import client.component.dashboard.topBar.TopBarController;
import client.component.dashboard.users.UsersController;
import client.component.execution.ExecutionController;
import client.utils.Constants;
import client.utils.http.HttpClientUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dto.FunctionDto;
import dto.FunctionInfo;
import dto.ProgramDto;
import dto.ProgramFunctionDto;
import javafx.application.Platform;
import javafx.fxml.FXML;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Scanner;

public class DashboardController {

    private String programInContext;
    private boolean isProgram;
    private ExecutionController executionController;

    @FXML private TopBarController topBarController;
    @FXML private UsersController usersController;
    @FXML private ProgramsFunctionsController programsFunctionsController;

    @FXML
    public void initialize() {
        if (topBarController != null) {
            topBarController.setMainController(this);
        }
        if (usersController != null) {
            usersController.setMainController(this);
        }
        if (programsFunctionsController != null) {
            programsFunctionsController.setMainController(this);
        }
    }

    public void setExecutionController(ExecutionController executionController) {
        this.executionController = executionController;
    }

    public void initializeUser(String username) {
        this.topBarController.setUserName(username);
    }

    public String getUserName() {
        return this.topBarController.getUserName();
    }

    public void initialExecutionScreen(String programName, boolean isProgram) {
        this.isProgram = isProgram;
        String finalUrl = HttpUrl
                .parse(Constants.DISPLAY_SERVLET)
                .newBuilder()
                .addQueryParameter("program_name", programName)
                .addQueryParameter("is_program", String.valueOf(isProgram))
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
                    ProgramFunctionDto programDetails;

                    if (isProgram) {
                        // ProgramDto implements ProgramFunctionDto
                        ProgramDto programDto = gson.fromJson(json, ProgramDto.class);
                        programDetails = programDto;
                    } else {
                        // FunctionDto implements ProgramFunctionDto
                        FunctionDto functionDto = gson.fromJson(json, FunctionDto.class);
                        programDetails = functionDto;
                    }

                    Platform.runLater(() -> {
                        executionController.initialProgramDetails(programDetails, isProgram);
                    });
                } finally {
                    response.close();
                }
            }
        });
    }
}

