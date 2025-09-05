package fx.component.topbar;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import fx.system.SEmulatorSystemController;

import java.util.List;


public class TopBarController {
    private SEmulatorSystemController mainController;

    @FXML
    private TextField loadFileTextField;


    @FXML
    private Label degreeLabel;


    public void setMainController(SEmulatorSystemController mainController) {
        this.mainController = mainController;
    }


    public void btnLoadFileListener(ActionEvent event) {
        String fileName = loadFileTextField.getText().trim();
        if (fileName.isEmpty()) {
            return;
        }

        if (mainController != null) {
            mainController.btnLoadFileListener(fileName);
        }
    }

    public void setLoadFileText(String newText) {
        loadFileTextField.setText(newText);
    }

    public void updateDegreeLabel(int programDegree, int maxDegree){
        degreeLabel.setText(programDegree + "/" + maxDegree);
    }

    public void btnExpandListener(ActionEvent event) {
        String degree = degreeLabel.getText();
        String[] degreeValues = degree.split("/");
        if (degreeValues.length != 2) {
            return; // Invalid format
        }
        int currentDegree;
        int maxDegree;
        try {
            currentDegree = Integer.parseInt(degreeValues[0].trim());
            maxDegree = Integer.parseInt(degreeValues[1].trim());
        } catch (NumberFormatException e) {
            return; // Invalid numbers
        }

        if (currentDegree < maxDegree && mainController != null) {
            mainController.btnExpandListener(currentDegree + 1);
        }
    }

}
