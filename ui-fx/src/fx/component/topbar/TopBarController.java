package fx.component.topbar;

import fx.app.display.Theme;
import fx.app.util.ProgramUtil;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import fx.system.SEmulatorSystemController;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import semulator.api.dto.ProgramFunctionDto;
import java.io.File;
import java.util.List;


public class TopBarController {
    private SEmulatorSystemController mainController;

    @FXML private GridPane topBarRoot;
    @FXML private ComboBox<Theme> styleSheetCmb;

    private boolean refreshingProgramOrFunctions = false;
    private boolean refreshingExpandOptions = false;
    private boolean refreshingCollapseOptions = false;

    // Holds "current degree" and "max degree" as Strings
    private final StringProperty currentDegree = new SimpleStringProperty(this, "currentDegree", "current Degree");
    private final StringProperty maxDegree = new SimpleStringProperty(this, "maxDegree", "max Degree");
    private final StringProperty currentFileName = new SimpleStringProperty(this, "currentFileName", "");

    @FXML private TextField loadFileTextField;
    @FXML private Label degreeLabel;
    @FXML private ComboBox<String> cmbHighlight;
    @FXML private ComboBox<String> cmbProgramOrFunction;
    @FXML private ComboBox<Integer> cmbExpand;
    @FXML private ComboBox<Integer> cmbCollapse;


    private final ObservableList<String> highlightOptions = FXCollections.observableArrayList();
    private final ObservableList<String> programOrFunctionOptions = FXCollections.observableArrayList();
    private final ObservableList<Integer> expandOptions = FXCollections.observableArrayList();
    private final ObservableList<Integer> collapseOptions = FXCollections.observableArrayList();

    @FXML private void initialize() {
        cmbHighlight.setItems(highlightOptions);
        cmbProgramOrFunction.setItems(programOrFunctionOptions);
        cmbExpand.setItems(expandOptions);
        cmbCollapse.setItems(collapseOptions);

        degreeLabel.textProperty().bind(
                Bindings.format("%s / %s", currentDegree, maxDegree)
        );

        loadFileTextField.textProperty().bind(currentFileName);

        styleSheetCmb.getItems().addAll(Theme.values());
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
            //mainController.btnLoadFileListener(xmlFile);
            mainController.loadFileAsync(xmlFile);
        }
    }

    public void setLoadFileText(String newText) {
        currentFileName.set(newText);
    }

    public void updateCurrentDegreeLabel(int programDegree){
        setCurrentDegree(programDegree);
        int maxDegree = getMaxDegree();

        List<Integer> newExpandValues = ProgramUtil.generateNewExpandOptions(programDegree, maxDegree);
        List<Integer> newCollapseValues = ProgramUtil.generateNewCollapseOptions(programDegree, maxDegree);

        refreshExpandAndCollapseOptions(newExpandValues, newCollapseValues);
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

    public void btnExpandListener(ActionEvent event) {
        if(expandOptions.isEmpty()){
            return;
        }

        Integer expandSelected = cmbExpand.getSelectionModel().getSelectedItem();
        if(expandSelected == null)
            return;
        cmbExpand.setDisable(true);
        try {
            if (mainController != null) {
                mainController.btnExpandListener(expandSelected);
            }
        } finally {
            cmbExpand.setDisable(false);

        }
    }

    public void btnCollapseListener(ActionEvent event) {

        if(collapseOptions.isEmpty()){
            return;
        }

        Integer collapseSelected = cmbCollapse.getSelectionModel().getSelectedItem();
        if(collapseSelected == null)
            return;
        if (mainController != null) {
            mainController.btnCollapseListener(collapseSelected);
        }
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

    public void refreshProgramOrFunctionOptions(List<String> programOrFunction) {
        refreshingProgramOrFunctions = true;
        try {
            programOrFunctionOptions.clear();
            cmbProgramOrFunction.getSelectionModel().clearSelection();
            cmbProgramOrFunction.setValue(null);

            if (programOrFunction == null || programOrFunction.isEmpty()) {
                programOrFunctionOptions.setAll();
                cmbProgramOrFunction.setDisable(true);
                return;
            }
            else {
                programOrFunctionOptions.setAll(programOrFunction);
            }

            boolean hasItems = !programOrFunctionOptions.isEmpty();
            cmbProgramOrFunction.setDisable(!hasItems);

            if (hasItems) {
                cmbProgramOrFunction.getSelectionModel().select(0);
            } else {
                cmbProgramOrFunction.setValue(null);
            }
        }
        finally{
            refreshingProgramOrFunctions = false;
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

        cmbExpand.setPromptText("Expand");
        cmbCollapse.setPromptText("Collapse");
    }

    @FXML void onProgramFunctionChangedListener(ActionEvent event) {
        if(refreshingProgramOrFunctions)
            return;
        String programFunctionSelected = cmbProgramOrFunction.getSelectionModel().getSelectedItem();
        if(mainController != null) {
            mainController.onProgramFunctionChangedListener(programFunctionSelected);
        }
    }

    @FXML void onStyleSheetChangedListener(ActionEvent event) {
        Theme selectedStyleSheet = styleSheetCmb.getSelectionModel().getSelectedItem();
        if(mainController != null) {
            mainController.onStyleSheetChangedListener(selectedStyleSheet);
        }
        topBarRoot.getStylesheets().clear();
        switch(selectedStyleSheet) {
            case Theme.Default:
                topBarRoot.getStylesheets().add("/fx/component/topbar/topBar.css");
                break;
            case Theme.Dark:
                topBarRoot.getStylesheets().add("/fx/component/topbar/topBarV2.css");
                break;
            case Theme.Pink:
                topBarRoot.getStylesheets().add("/fx/component/topbar/topBarV3.css");
                break;
        }
    }

    @FXML void btnCreateAProgramListener(ActionEvent event) {
        try {
            mainController.btnCreateAProgramListener();
        }
        catch (Exception e) {

        }
    }
}
