package client.component.dashboard.users;

import client.component.dashboard.DashboardController;
import client.utils.Constants;
import com.google.gson.reflect.TypeToken;
import dto.FunctionInfo;
import dto.UserInfo;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import com.google.gson.Gson;
import javafx.util.Duration;

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

    private ObservableList<UserInfo> usersData = FXCollections.observableArrayList();

    public void setMainController(DashboardController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void initialize() {
        availableUsersColCredits.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getCredits()));
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

        availableUsersTbl.setItems(usersData);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(2), event -> refreshUsers())
        );

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
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
                                currentUser.setCredits(user.getCredits());
                                currentUser.setFunctionsNumber(user.getFunctionsNumber());
                                currentUser.setProgramsNumber(user.getProgramsNumber());
                                currentUser.setRunsNumber(user.getRunsNumber());
                                currentUser.setUsedCredits(user.getUsedCredits());
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
}
