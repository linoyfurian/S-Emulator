package client.component.dashboard;

import client.component.dashboard.programsFunctions.ProgramsFunctionsController;
import client.component.dashboard.topBar.TopBarController;
import client.component.dashboard.users.UsersController;
import javafx.fxml.FXML;

import java.io.Closeable;
import java.io.IOException;

public class DashboardController {

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



    public void initializeUser(String username) {
        this.topBarController.setUserName(username);
    }
}
