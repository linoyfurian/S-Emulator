package client.component.dashboard;

import client.component.dashboard.programsFunctions.ProgramsFunctionsController;
import client.component.dashboard.topBar.TopBarController;
import client.component.dashboard.users.UsersController;
import client.component.execution.ExecutionController;
import client.utils.Constants;
import client.utils.http.HttpClientUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dto.*;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class DashboardController {

    private String programInContext;
    private boolean isProgram;
    private ExecutionController executionController;
    private boolean isNewUser;
    private String selectedUser = null;

    @FXML private TopBarController topBarController;
    @FXML private UsersController usersController;
    @FXML private ProgramsFunctionsController programsFunctionsController;

    @FXML private ScrollPane systemScrollPane;

    private final IntegerProperty credits = new SimpleIntegerProperty(0);

    private List<RunResultDto> currentHistory;

    public IntegerProperty creditsProperty() { return credits; }
    public int getCredits() { return credits.get(); }
    public void setCredits(int value) {
        Platform.runLater(() -> {
            credits.set(value);
            String username = topBarController.getUserName();
            usersController.updateCredits(username, credits.get());

        }); }
    public void addCredits(int delta) {
        Platform.runLater(() -> {
            credits.set(credits.get() + delta);
            String username = topBarController.getUserName();
            usersController.updateCredits(username, credits.get());
        });
    }

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
        this.selectedUser = username;
    }

    public String getUserName() {
        return this.topBarController.getUserName();
    }

    public void setSelectedUser(String selectedUser) {
        this.selectedUser = selectedUser;
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

    public void updateHistory() {
        UserInfo selectedUser = usersController.getSelectedUser();
        String username;

        if (selectedUser == null) {
            username = this.topBarController.getUserName();
            if(this.selectedUser == null)
                isNewUser = false;
            else if(this.selectedUser.equals(username))
                isNewUser = false;
            else
                isNewUser = true;
        }
        else {
            username = selectedUser.getName();
            if(username.equals(this.topBarController.getUserName())) {
                isNewUser = false;
            }
            else
                isNewUser = true;
        }

        String finalUrl = HttpUrl
                .parse(Constants.HISTORY_RUN_SERVLET)
                .newBuilder()
                .addQueryParameter("username", username)
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
                    currentHistory = gson.fromJson(json, new TypeToken<List<RunResultDto>>() {}.getType());

                    Platform.runLater(() -> {
                        usersController.updateHistory(currentHistory, isNewUser);
                    });
                } finally {
                    response.close();
                }
            }
        });
    }

    public void updateRunsNumber(){
        String finalUrl = HttpUrl
                .parse(Constants.USERS_PAGE)
                .newBuilder()
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

    public void onUserSelectedListener(UserInfo username){

        String user;
        if(username==null)
            user = getUserName();
        else
            user = username.getName();
        String finalUrl = HttpUrl
                .parse(Constants.HISTORY_RUN_SERVLET)
                .newBuilder()
                .addQueryParameter("username", user)
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
                    currentHistory = gson.fromJson(json, new TypeToken<List<RunResultDto>>() {}.getType());

                    Platform.runLater(() -> {
                        boolean isNewUser = true;
                        if(username==null){
                            if(selectedUser==null)
                                isNewUser = false;
                            else{
                                if(selectedUser.equals(getUserName()))
                                    isNewUser = false;
                            }
                            selectedUser = getUserName();
                        }
                        else{
                            if(selectedUser.equals(username.getName()))
                                isNewUser = false;
                            selectedUser = username.getName();
                        }
                        usersController.updateHistory(currentHistory, isNewUser);
                    });
                } finally {
                    response.close();
                }
            }
        });
    }

    public void onReRunButtonListener(RunResultDto selectedRun) throws IOException{
        if(selectedRun==null)
            return;
        final String FXML_PATH = Constants.EXECUTION_FXML_RESOURCE_LOCATION;

        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH));
        ScrollPane root = loader.load();

        ExecutionController controller = loader.getController();
        controller.setMainController(this);

        Stage dialog = new Stage();
        dialog.setTitle("Execution");

        String userName = getUserName();
        controller.setUserName(userName);

        // ProgramInfo programInContext = this.programsTbl.getSelectionModel().getSelectedItem();
        setExecutionController(controller);
        boolean isProgram = false;
        if(selectedRun.getProgramOrFunction().equals("Program"))
            isProgram = true;


        this.programInContext = selectedRun.getName();
        this.isProgram = isProgram;

        initialExecutionScreen(selectedRun.getName(), isProgram);

        controller.initialReRun(selectedRun);

        Scene scene = new Scene(root);
        dialog.initOwner(this.systemScrollPane.getScene().getWindow());
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setAlwaysOnTop(true);
        dialog.setScene(scene);
        dialog.showAndWait();




    }
}

