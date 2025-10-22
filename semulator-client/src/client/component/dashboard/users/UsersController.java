package client.component.dashboard.users;

import client.component.dashboard.DashboardController;
import client.utils.Constants;
import client.utils.display.ProgramUtil;
import client.utils.display.VariableRow;
import client.utils.http.HttpClientUtil;
import com.google.gson.reflect.TypeToken;
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
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class UsersController {
    private DashboardController mainController;

    @FXML private Button reRunBtn;
    @FXML private Button showStatusBtn;

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

    @FXML private TableColumn<VariableRow, String> variableNameCol;
    @FXML private TableColumn<VariableRow, Long> variableValueCol;
    @FXML private TableView<VariableRow> variablesStatusTable;
    @FXML private VBox showStatusVariablesVbox;

    private ObservableList<VariableRow> variablesData = FXCollections.observableArrayList();
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
                new SimpleLongProperty(cellData.getValue().getUsedCredits()));
        availableUsersColCredits.setCellValueFactory(cellData ->
                new SimpleLongProperty(cellData.getValue().getCredits()));

        availableUsersTbl.setItems(usersData);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), event -> refreshUsers())
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

        variableNameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));
        variableValueCol.setCellValueFactory(cellData ->
                new SimpleLongProperty(cellData.getValue().getValue()).asObject());
        variablesStatusTable.setItems(variablesData);

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

                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<UserInfo>>() {}.getType();
                    List<UserInfo> users = gson.fromJson(json.toString(), listType);

                    Platform.runLater(() -> {
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

    public UserInfo getSelectedUser(){
        return this.availableUsersTbl.getSelectionModel().getSelectedItem();
    }

    public void updateHistory(List<RunResultDto> currentRunHistoryToDisplay, boolean isUserChanged){

        if(isUserChanged){
            this.historyRunData.clear();
            this.historyRunData.addAll(currentRunHistoryToDisplay);
            this.historyTbl.refresh();
        }
        else{
            List<RunResultDto> historyDelta = getHistoryDelta(this.historyRunData, currentRunHistoryToDisplay);
            this.historyRunData.addAll(historyDelta);
            this.historyTbl.refresh();
        }
    }

    public List<RunResultDto> getHistoryDelta(List<RunResultDto> original, List<RunResultDto> currentRunHistoryToDisplay){
        List<RunResultDto> historyDelta = new ArrayList<>();
        Set<Integer> alreadyExisted = new HashSet<>();
        for(RunResultDto run : original){
            alreadyExisted.add(run.getRunNumber());
        }
        for(RunResultDto run : currentRunHistoryToDisplay){
            if(!alreadyExisted.contains(run.getRunNumber())){
                historyDelta.add(run);
            }
        }
        return historyDelta;
    }

    public void updateCredits(String username, long credits){
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
        this.showStatusVariablesVbox.setVisible(false);
        this.showStatusBtn.setVisible(false);
        this.reRunBtn.setVisible(false);
        mainController.onUserSelectedListener(null);
    }

    @FXML
    void onUserSelectedListener(MouseEvent event) {
        UserInfo userSelected = getSelectedUser();
        this.showStatusVariablesVbox.setVisible(false);
        this.showStatusBtn.setVisible(false);
        this.reRunBtn.setVisible(false);
        mainController.onUserSelectedListener(userSelected);
    }

    @FXML
    void onReRunBtnListener(ActionEvent event) {
        RunResultDto selectedRun = historyTbl.getSelectionModel().getSelectedItem();
        if(selectedRun != null){
            try{
                mainController.onReRunButtonListener(selectedRun);
            }
            catch (Exception e){

            }

        }
    }

    @FXML
    void onSelectedHistoryRunListener(MouseEvent event) {
        this.showStatusBtn.setVisible(true);
        this.reRunBtn.setVisible(true);
        this.showStatusVariablesVbox.setVisible(false);
    }

    @FXML
    void onShowStatusBtnListener(ActionEvent event) {
        this.showStatusVariablesVbox.setVisible(true);
        RunResultDto selectedRun = historyTbl.getSelectionModel().getSelectedItem();
        if(selectedRun != null){
            List<VariableRow> variables = ProgramUtil.generateVariablesRowList(selectedRun);

            this.variablesData.clear();
            this.variablesData.addAll(variables);
        }
    }
}
