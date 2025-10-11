package client.component.dashboard.users;

import client.component.dashboard.DashboardController;
import client.utils.Constants;
import client.utils.http.HttpClientUtil;
import com.google.gson.reflect.TypeToken;
import dto.DebugContextDto;
import dto.FunctionInfo;
import dto.RunResultDto;
import dto.UserInfo;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import com.google.gson.Gson;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class UsersController {
    private DashboardController mainController;

    @FXML private Button unselectUserBtn;

    @FXML private TableColumn<UserInfo, Number> availableUsersColCredits;
    @FXML private TableColumn<UserInfo, Number> availableUsersColFunctionsNumber;
    @FXML private TableColumn<UserInfo, String> availableUsersColName;
    @FXML private TableColumn<UserInfo, Number> availableUsersColProgramsNumber;
    @FXML private TableColumn<UserInfo, Number> availableUsersColRunsNumber;
    @FXML private TableColumn<UserInfo, Number> availableUsersColUsedCredit;
    @FXML private TableView<UserInfo> availableUsersTbl;

    @FXML private TableColumn<RunResultDto, String> historyArchitectureCol;
    @FXML private TableColumn<RunResultDto, Integer> historyCyclesCol;
    @FXML private TableColumn<RunResultDto, Integer> historyDegreeCol;
    @FXML private TableColumn<RunResultDto, String> historyIsProgramCol;
    @FXML private TableColumn<RunResultDto, String> historyNameCol;
    @FXML private TableColumn<RunResultDto, Long> historyResultCol;
    @FXML private TableColumn<RunResultDto, Integer> historyRunNumberCol;
    @FXML private TableView<RunResultDto> historyTbl;


    private ObservableList<UserInfo> usersData = FXCollections.observableArrayList();
    private ObservableList<RunResultDto> historyRunData = FXCollections.observableArrayList();

    public void setMainController(DashboardController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void initialize() {
        availableUsersColFunctionsNumber.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getFunctionsNumber()));
        availableUsersColProgramsNumber.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getProgramsNumber()));
        availableUsersColRunsNumber.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getRunsNumber()));
        availableUsersColName.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));
        availableUsersColUsedCredit.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getUsedCredits()));
        availableUsersColCredits.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getCredits()));

        availableUsersTbl.setItems(usersData);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(2), event -> refreshUsers())
        );

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        historyCyclesCol.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getCycles()).asObject());
        historyRunNumberCol.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getRunNumber()).asObject());
        historyDegreeCol.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getDegreeOfRun()).asObject());
        historyResultCol.setCellValueFactory(cellData ->
                new SimpleLongProperty(cellData.getValue().getResultY()).asObject());
        historyArchitectureCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getArchitecture()));
        historyIsProgramCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getProgramOrFunction()));
        historyNameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));

        historyTbl.setItems(historyRunData);

        Timeline historyTimeline = new Timeline(
                new KeyFrame(Duration.seconds(2), event -> mainController.onUserSelectedListener(getSelectedUser()))
        );

        historyTimeline.setCycleCount(Timeline.INDEFINITE);
        historyTimeline.play();

    }

    private void refreshUsers() {
        new Thread(() -> {
            try {
                URL url = new URL(Constants.USERS_PAGE);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                try (Scanner scanner = new Scanner(conn.getInputStream())) {
                    StringBuilder json = new StringBuilder();
                    while (scanner.hasNext()) {
                        json.append(scanner.nextLine());
                    }

                    // Gson: fromJson -> List<UserInfo>
                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<UserInfo>>() {}.getType();
                    List<UserInfo> users = gson.fromJson(json.toString(), listType);

                    Platform.runLater(() -> {
//                        usersData.clear();
//                        usersData.addAll(users);
                        Set<String> usersSet = new HashSet<>();
                        for (UserInfo user : usersData) {
                            usersSet.add(user.getName());
                        }

                        for (UserInfo user : users) {
                            if(!usersSet.contains(user.getName())) {
                                usersData.add(user);
                            }
                            else{
                                UserInfo currentUser = findCurrentUser(user.getName(), usersData);
                                currentUser.setFunctionsNumber(user.getFunctionsNumber());
                                currentUser.setProgramsNumber(user.getProgramsNumber());
                                currentUser.setUsedCredits(user.getUsedCredits());
                                currentUser.setCredits(user.getCredits());
                                currentUser.setRunsNumber(user.getRunsNumber());
                            }
                        }

                        availableUsersTbl.refresh();
                    });
                }
            } catch (Exception e) {
            }
        }).start();
    }

    private UserInfo findCurrentUser(String userName, List<UserInfo> usersData) {
        for(UserInfo user : usersData) {
            if(user.getName().equals(userName)) {
                return user;
            }
        }
        return null;
    }

    private List<UserInfo> getUsersDelta(List<UserInfo> original, List<UserInfo> newUsers) {
        List<UserInfo> usersDelta = new ArrayList<>();
        Set<String> usersSet = new HashSet<>();

        for (UserInfo user : original) {
            usersSet.add(user.getName());
        }

        for (UserInfo user : newUsers) {
            if (!usersSet.contains(user.getName())) {
                usersDelta.add(user);
            }
        }
        return usersDelta;
    }

    public UserInfo getSelectedUser(){
        return this.availableUsersTbl.getSelectionModel().getSelectedItem();
    }

    public void updateHistory(List<RunResultDto> currentRunHistoryToDisplay){
        this.historyRunData.clear();
        this.historyRunData.addAll(currentRunHistoryToDisplay);
        this.historyTbl.refresh();
    }

    public void updateCredits(String username, int credits){
        String finalUrl = HttpUrl
                .parse(Constants.CREDITS_SERVLET)
                .newBuilder()
                .addQueryParameter("operation", "new")
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

    @FXML
    void onUnselectedUserBtnListener(ActionEvent event) {
        this.availableUsersTbl.getSelectionModel().clearSelection();
        mainController.onUserSelectedListener(null);
    }

    @FXML
    void onUserSelectedListener(MouseEvent event) {
        UserInfo userSelected = getSelectedUser();
        mainController.onUserSelectedListener(userSelected);
    }


}
