package client.component.execution.topbar;

import client.component.execution.ExecutionController;
import client.utils.display.ProgramUtil;
import dto.ProgramFunctionDto;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.util.List;

public class TopbarExecutionController {
    private ExecutionController mainController;

    // Holds "current degree" and "max degree" as Strings
    private final StringProperty currentDegree = new SimpleStringProperty(this, "currentDegree", "current Degree");
    private final StringProperty maxDegree = new SimpleStringProperty(this, "maxDegree", "max Degree");
    private final StringProperty currentFileName = new SimpleStringProperty(this, "currentFileName", "");

    @FXML private Label availableCreditsLbl;
    @FXML private ComboBox<Integer> cmbCollapse;
    @FXML private ComboBox<Integer> cmbExpand;
    @FXML private ComboBox<String> cmbHighlight;
    @FXML private Label degreeLabel;
    @FXML private Label userNameLbl;

    private boolean refreshingExpandOptions = false;
    private boolean refreshingCollapseOptions = false;

    private final ObservableList<String> highlightOptions = FXCollections.observableArrayList();
    private final ObservableList<Integer> expandOptions = FXCollections.observableArrayList();
    private final ObservableList<Integer> collapseOptions = FXCollections.observableArrayList();

    @FXML private void initialize() {
        cmbHighlight.setItems(highlightOptions);
        cmbExpand.setItems(expandOptions);
        cmbCollapse.setItems(collapseOptions);

        degreeLabel.textProperty().bind(
                Bindings.format("%s / %s", currentDegree, maxDegree)
        );
    }


    public void setMainController(ExecutionController mainController) {
        this.mainController = mainController;
    }

    public void setUserName(String userName) {
        this.userNameLbl.setText(userName);
    }

    public int getCurrentDegree() {
        try {
            return Integer.parseInt(currentDegree.get());
        } catch (NumberFormatException e) {
            return 0; // fallback in case value is not a number
        }
    }

    public void setCurrentDegree(int value) {
        currentDegree.set(Integer.toString(value));
    }

    public int getMaxDegree() {
        try {
            return Integer.parseInt(maxDegree.get());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void setMaxDegree(int value) {
        maxDegree.set(Integer.toString(value));
    }


    public void updateDegreeLabel(int programDegree, int maxDegree){
        refreshingExpandOptions = true;
        refreshingCollapseOptions = true;
        try{
            setCurrentDegree(programDegree);
            setMaxDegree(maxDegree);

            List<Integer> newExpandValues = ProgramUtil.generateNewExpandOptions(programDegree, maxDegree);
            List<Integer> newCollapseValues = ProgramUtil.generateNewCollapseOptions(programDegree, maxDegree);

            refreshExpandAndCollapseOptions(newExpandValues, newCollapseValues);
        }
        finally{
            refreshingExpandOptions = false;
            refreshingCollapseOptions = false;
        }
    }


    public void refreshExpandAndCollapseOptions(List<Integer> expandOptions, List<Integer> collapseOptions) {
        this.expandOptions.clear();
        this.collapseOptions.clear();

        if(expandOptions.isEmpty()){
            this.expandOptions.addAll();
        }
        else
            this.expandOptions.addAll(expandOptions);

        if(collapseOptions.isEmpty()){
            this.collapseOptions.addAll();
        }
        else
            this.collapseOptions.addAll(collapseOptions);

        cmbExpand.setDisable(expandOptions.isEmpty());
        cmbCollapse.setDisable(collapseOptions.isEmpty());

        cmbExpand.getSelectionModel().clearSelection();
        cmbCollapse.getSelectionModel().clearSelection();

    }


    public void refreshHighlightOptions(ProgramFunctionDto programInContextDetails) {
        int i;
        highlightOptions.clear();
        highlightOptions.add("— choose —");


        List<String> variables = ProgramUtil.getDisplayedProgramVariables(programInContextDetails);

        for (i = 0; i < variables.size(); i++) {
            highlightOptions.add(variables.get(i));
        }

        List<String> labels = ProgramUtil.getDisplayedProgramLabels(programInContextDetails);

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


}
