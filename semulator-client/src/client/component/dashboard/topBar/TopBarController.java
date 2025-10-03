package client.component.dashboard.topBar;

import client.component.dashboard.DashboardController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class TopBarController {
    private DashboardController mainController;


    @FXML private Label userNameLbl;


    public void setMainController(DashboardController mainController) {
        this.mainController = mainController;
    }

    public void setUserName(String userName) {
        userNameLbl.setText(userName);
    }

}
