package client.component.execution.topbar;

import client.component.execution.ExecutionController;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

public class TopbarExecutionController {
    private ExecutionController mainController;

    @FXML private Label availableCreditsLbl;
    @FXML private ComboBox<?> cmbCollapse;
    @FXML private ComboBox<?> cmbExpand;
    @FXML private ComboBox<?> cmbHighlight;
    @FXML private Label degreeLabel;
    @FXML private Label userNameLbl;

    public void setMainController(ExecutionController mainController) {
        this.mainController = mainController;
    }

    public void setUserName(String userName) {
        this.userNameLbl.setText(userName);
    }
}
