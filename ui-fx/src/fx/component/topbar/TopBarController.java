package fx.component.topbar;

import fx.app.util.ProgramUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import fx.system.SEmulatorSystemController;
import javafx.stage.FileChooser;
import semulator.api.dto.FunctionDto;
import semulator.api.dto.ProgramDto;
import semulator.api.dto.ProgramFunctionDto;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class TopBarController {
    private SEmulatorSystemController mainController;
    private ProgressBar inlineProgressBar; //todo !!!!!!!!!!!


    @FXML private TextField loadFileTextField;
    @FXML private Label degreeLabel;
    @FXML private ComboBox<String> cmbHighlight;
    @FXML private ComboBox<String> cmbProgramOrFunction;

    private final ObservableList<String> highlightOptions = FXCollections.observableArrayList();
    private final ObservableList<String> programOrFunctionOptions = FXCollections.observableArrayList();

    @FXML private void initialize() {
        cmbHighlight.setItems(highlightOptions);
        cmbProgramOrFunction.setItems(programOrFunctionOptions);
    }

    public void setMainController(SEmulatorSystemController mainController) {
        this.mainController = mainController;
    }


    public void btnLoadFileListener(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select program xml file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(loadFileTextField.getScene().getWindow());
        if (selectedFile == null) {
            return;
        }
        String xmlFile = selectedFile.getAbsolutePath();

        if (mainController != null) {
            mainController.btnLoadFileListener(xmlFile);
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

    public void btnCollapseListener(ActionEvent event) {
        String degree = degreeLabel.getText();
        String[] degreeValues = degree.split("/");
        if (degreeValues.length != 2) {
            return; // Invalid format
        }
        int currentDegree;
        try {
            currentDegree = Integer.parseInt(degreeValues[0].trim());
        } catch (NumberFormatException e) {
            return; // Invalid numbers
        }

        if (currentDegree > 0 && mainController != null) {
            mainController.btnCollapseListener(currentDegree - 1);
        }
    }

    public void refreshHighlightOptions(String displayedProgramName, ProgramFunctionDto program) {
        int i;
        highlightOptions.clear();
        highlightOptions.add("— choose —");


        List<String> variables = ProgramUtil.getDisplayedProgramVariables(displayedProgramName, program);

        for (i = 0; i < variables.size(); i++) {
            highlightOptions.add(variables.get(i));
        }

        List<String> labels = ProgramUtil.getDisplayedProgramLabels(displayedProgramName, program);

        for (i = 0; i < labels.size(); i++) {
            highlightOptions.add(labels.get(i));
        }

        cmbHighlight.getSelectionModel().clearSelection();
    }

    @FXML void onHighlightChangedListener(ActionEvent event) {
        String highlightSelected = cmbHighlight.getSelectionModel().getSelectedItem();
        if(mainController != null) {
            mainController.onHighlightChangedListener(highlightSelected);
        }
    }

    public int getCurrentDegree(){
        int currentDegree=0;
        String degree = degreeLabel.getText();
        String[] degreeValues = degree.split("/");
        try {
            currentDegree = Integer.parseInt(degreeValues[0].trim());
        } catch (NumberFormatException e) {

        }

        return currentDegree;
    }


    public void refreshProgramOrFunctionOptions(ProgramDto program) {
        int i;
        programOrFunctionOptions.clear();
        programOrFunctionOptions.add(program.getName());

        List<FunctionDto> functions = program.getFunctions();


        for (i = 0; i < functions.size(); i++) {
            programOrFunctionOptions.add(functions.get(i).getName());
        }

        cmbProgramOrFunction.getSelectionModel().select(programOrFunctionOptions.get(0));
    }

    @FXML void onProgramFunctionChangedListener(ActionEvent event) {
        String programFunctionSelected = cmbProgramOrFunction.getSelectionModel().getSelectedItem();
        if(mainController != null) {
            mainController.onProgramFunctionChangedListener(programFunctionSelected);
        }
    }
}
