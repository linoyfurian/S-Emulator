package client.component.dashboard.users;

import client.component.dashboard.DashboardController;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class UsersController {
    private DashboardController mainController;


    @FXML private TableColumn<?, ?> availableUsersColCredits;
    @FXML private TableColumn<?, ?> availableUsersColFunctionsNumber;
    @FXML private TableColumn<?, ?> availableUsersColName;
    @FXML private TableColumn<?, ?> availableUsersColProgramsNumber;
    @FXML private TableColumn<?, ?> availableUsersColRunsNumber;
    @FXML private TableColumn<?, ?> availableUsersColUsedCredit;
//    @FXML private TableView<UserInfo> availableUsersTbl;



    public void setMainController(DashboardController mainController) {
        this.mainController = mainController;
    }

}
