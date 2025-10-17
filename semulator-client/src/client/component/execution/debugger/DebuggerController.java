package client.component.execution.debugger;

import client.component.execution.ExecutionController;
import client.utils.Constants;
import client.utils.display.VariableRow;
import com.google.gson.Gson;
import dto.DebugContextDto;
import dto.ExecutionRunDto;
import dto.ProgramFunctionDto;
import dto.RunProgramRequest;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.Window;
import okhttp3.HttpUrl;

import java.util.*;
import java.util.function.UnaryOperator;

public class DebuggerController {
    private ExecutionController mainController;

    private final String INPUT_VARIABLES = "input-variable";
    private final Map<String, TextField> inputFields = new HashMap<>();

    private final ObservableSet<String> changedVarNames = FXCollections.observableSet();

    @FXML private ComboBox<String> architectureCbox;

    @FXML private ScrollPane inputsScroll;
    @FXML private VBox inputsContainer;

    private ObservableList<VariableRow> variablesData = FXCollections.observableArrayList();

    @FXML private TableColumn<VariableRow, String> nameCol;
    @FXML private TableColumn<VariableRow, String> valueCol;
    @FXML private TableView<VariableRow> variablesTable;

    @FXML private Label cyclesLabel;
    private IntegerProperty cyclesProperty = new SimpleIntegerProperty(0);

    @FXML private Button runBtn;
    @FXML private RadioButton radioBtnDebug;
    @FXML private RadioButton radioBtnRegular;
    @FXML private Button resumeBtn;
    @FXML private Button stepOverBtn;
    @FXML private Button stopBtn;
    @FXML private Button stepBackBtn;


    @FXML private Button backToDashboardBtn;

    private boolean isDebugMode = false;
    private boolean isStopDebugger = false;
    private boolean isResume = false;

    private final StringProperty lastChangedVarName = new SimpleStringProperty(null);
    private static final PseudoClass PC_CHANGED = PseudoClass.getPseudoClass("changed");

    @FXML
    private void initialize() {
        nameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));
        valueCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getValue())));
        variablesTable.setItems(variablesData);
        cyclesLabel.textProperty().bind(Bindings.format("%s", cyclesProperty));

        initVariablesHighlighting();
        initArchitectureOptions();

    }

    public void setMainController(ExecutionController mainController) {
        this.mainController = mainController;
    }


    private void initVariablesHighlighting() {
        variablesTable.setRowFactory(tv -> {
            TableRow<VariableRow> row = new TableRow<>();

            Runnable apply = () -> {
                VariableRow item = row.getItem();
                boolean highlight = item != null && changedVarNames.contains(item.getName());
                row.pseudoClassStateChanged(PC_CHANGED, highlight);
            };

            row.itemProperty().addListener((o, a, b) -> apply.run());
            row.indexProperty().addListener((o, a, b) -> apply.run());
            changedVarNames.addListener((SetChangeListener<String>) c -> apply.run());

            return row;
        });
    }


    /**
     * When a program is loaded or when switching program
     */
    public void setProgram(ProgramFunctionDto programInContextDetails) {
        if (cyclesLabel != null) {
            cyclesProperty.set(0);
        }
        buildInputsUI(programInContextDetails);
        variablesData.clear();
        stepBackBtn.setDisable(true);
        runBtn.setDisable(true);
        stopBtn.setDisable(true);
        resumeBtn.setDisable(true);
        stepOverBtn.setDisable(true);
        radioBtnRegular.setDisable(false);
    }

    /**
     * Create rows of (Label | TextField) dynamically
     */
    private void buildInputsUI(ProgramFunctionDto programInContextDetails) {
        inputsContainer.getChildren().clear();
        inputFields.clear();

        List<String> inputNames = programInContextDetails.getInputVariablesInOrder(); // adapt to your ProgramDto

        for (String name : inputNames) {
            HBox row = new HBox(8);
            row.setFillHeight(true);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(3, 0, 0, 3));

            Label label = new Label(name + ":");
            label.setMinWidth(Region.USE_PREF_SIZE);
            label.getStyleClass().add(INPUT_VARIABLES);

            TextField field = new TextField();
            field.setPromptText("enter value");
            field.setPrefWidth(120);
            field.setPrefColumnCount(6);
            field.setMaxWidth(90);

            //allow only integers
            field.setTextFormatter(integerFormatter());

            // Pressing Enter moves to next field
            field.setOnAction(e -> focusNextField(field));

            HBox.setHgrow(field, Priority.NEVER);
            row.getChildren().addAll(label, field);

            inputsContainer.getChildren().add(row);
            inputFields.put(name, field);
        }
    }


    /** Allow only positive decimal numbers */
    private TextFormatter<String> integerFormatter() {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) return change;
            return null;
        };
        return new TextFormatter<>(filter);
    }

    private void focusNextField(TextField current) {
        // Simple focus traversal: go to next TextField in the VBox
        List<Node> children = inputsContainer.getChildren();
        for (int i = 0; i < children.size(); i++) {
            HBox row = (HBox) children.get(i);
            if (row.getChildren().contains(current)) {
                for (int j = i + 1; j < children.size(); j++) {
                    Node n = children.get(j);
                    if (n instanceof HBox h) {
                        for (Node c : h.getChildren()) {
                            if (c instanceof TextField tf) {
                                tf.requestFocus();
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    /** Very basic error dialog */
    private void showInputError(String name, String value) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Invalid input");
        alert.setContentText("Input '" + name + "' must be an integer. Got: " + value);
        alert.showAndWait();
    }

    private void initArchitectureOptions() {
        List<String> options = new ArrayList<>();
        options.add("I");
        options.add("II");
        options.add("III");
        options.add("IV");

        this.architectureCbox.setItems(FXCollections.observableList(options));
    }

    public Pane getInputsContainer() {
        return inputsContainer;
    }


    public void updateArchitecture(String selectedArchitecture) {
        this.architectureCbox.getSelectionModel().select(selectedArchitecture);
        updateRunBtnDisable();
    }

    @FXML
    void onArchitectureSelectedListener(ActionEvent event) {
        updateRunBtnDisable();
    }

    public void updateRunBtnDisable() {
        String selectedArchitecture = architectureCbox.getSelectionModel().getSelectedItem();
        if(selectedArchitecture==null)
            return;
        boolean isRunValid = this.mainController.checkIfRunIsValid(selectedArchitecture);
        this.runBtn.setDisable(!isRunValid);
    }

    @FXML
    void onBackToDashboardBtnListener(ActionEvent event) {
        Stage exec = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Window owner = exec.getOwner();
        exec.close();
        if (owner instanceof Stage s) {
            s.show();
            s.toFront();
        }
    }

    @FXML
    void onRunBtnListener(ActionEvent event) {
        isDebugMode = radioBtnDebug.isSelected();
        mainController.cleanDebugContext();

        Map<String, Long> originalInputs = new HashMap<>();

        if (mainController != null) {
            if(inputFields.isEmpty()) {
                long [] inputs = new long[0];
                mainController.onRunBtnListener(isDebugMode, originalInputs, inputs);
            }

            int maxIndex = 0;
            int index;
            String strValue;
            Long value = 0L;
            Map<Integer, Long> inputsValues = new HashMap<>();
            for (Map.Entry<String, TextField> input : inputFields.entrySet()) {
                index = Integer.parseInt(input.getKey().substring(1));
                strValue = input.getValue().getText();
                if (!strValue.isEmpty()||strValue!=null) {
                    try{
                        value = Long.parseLong(strValue.trim());
                    }
                    catch(NumberFormatException ex){
                        value = 0L;
                        input.getValue().setText("0");
                    }
                }
                else {
                    value = 0L;
                    input.getValue().setText("0");
                }
                inputsValues.put(index, value);
                originalInputs.put(input.getKey(), value);
                if (index > maxIndex)
                    maxIndex = index;
            }

            if (maxIndex == 0)
                return;

            long[] inputs = new long[maxIndex];
            for (int i = 1; i <= maxIndex; i++) {
                inputs[i - 1] = inputsValues.getOrDefault(i, 0L);
            }

            mainController.onRunBtnListener(isDebugMode, originalInputs, inputs);
        }
    }

    public void updateRunResult(ExecutionRunDto runResult){
        Map<String, Long> variablesValues = runResult.getVariables();
        variablesData.clear();

        for (Map.Entry<String, Long> entry : variablesValues.entrySet()) {
            VariableRow row = new VariableRow(entry.getKey(), entry.getValue());
            variablesData.add(row);
        }
        cyclesProperty.set(runResult.getCycles());
    }

    public void disableChangeOfInput(boolean disable){
        if(disable){
            for(TextField tf : inputFields.values()){
                tf.setDisable(true);
            }
        }
        else{
            for(TextField tf : inputFields.values()){
                tf.setDisable(false);
            }
        }
    }

    public void initialStartOfDebugging(){
        runBtn.setDisable(true);
        stopBtn.setDisable(false);
        resumeBtn.setDisable(false);
        stepOverBtn.setDisable(false);
        radioBtnRegular.setDisable(true);
        stepBackBtn.setDisable(true);
    }

    public void updateDebugResult(DebugContextDto debugContext) {
        Map<String, Long> currVariablesValues = debugContext.getCurrentVariablesValues();

        variablesData.clear();

        for (Map.Entry<String, Long> entry : currVariablesValues.entrySet()) {
            VariableRow row = new VariableRow(entry.getKey(), entry.getValue());
            variablesData.add(row);
        }

        cyclesProperty.set(debugContext.getCycles());

        long nextInstructionNumber = debugContext.getNextInstructionNumber();
        if (nextInstructionNumber == 0) { //finish debug
            runBtn.setDisable(false);
            stopBtn.setDisable(true);
            resumeBtn.setDisable(true);
            stepBackBtn.setDisable(true);
            stepOverBtn.setDisable(true);
            radioBtnRegular.setDisable(false);
            cyclesProperty.set(debugContext.getCycles());
        }
    }


    @FXML
    void onStepOverBtnListener(ActionEvent event) {
        stepBackBtn.setDisable(false);
        mainController.btnStepOverListener();
    }

    public void updateVariableHighlight(String variablesToHighLight){
        List<String> vars = new ArrayList<>();
        vars.add(variablesToHighLight);
        markVariablesChanged(vars);
    }


    public void markVariablesChanged(Collection<String> names) {
        Platform.runLater(() -> {
            changedVarNames.clear();
            if (names != null)
                changedVarNames.addAll(names);

            variablesTable.refresh();

            if (names != null && !names.isEmpty()) {
                String first = names.iterator().next();
                int idx = -1;
                for (int i = 0; i < variablesTable.getItems().size(); i++) {
                    if (first.equals(variablesTable.getItems().get(i).getName())) { idx = i; break; }
                }
                if (idx >= 0) variablesTable.scrollTo(Math.max(idx - 2, 0));
            }
        });

    }

    public String getArchitecture() {
        String architecture;
        architecture = this.architectureCbox.getValue();
        return architecture;
    }

    @FXML
    void onStopBtnListener(ActionEvent event) {
        finishDebug();
        mainController.btnStopListener();
    }

    public void finishDebug(){
        runBtn.setDisable(false);
        stopBtn.setDisable(true);
        resumeBtn.setDisable(true);
        stepOverBtn.setDisable(true);
        stepBackBtn.setDisable(true);
        radioBtnRegular.setDisable(false);
    }

    @FXML
    void onResumeBtnListener(ActionEvent event) {
        mainController.onBtnResumeListener();
    }

    @FXML
    void onNewRunBtnListener(ActionEvent event) {
        initialOfNewRun();
        mainController.btnNewRunListener();
    }


    public void initialOfNewRun(){
        variablesData.clear();
        if (cyclesLabel != null) {
            cyclesProperty.set(0);
        }

        TextField firstField = null;

        for (Node rowNode : inputsContainer.getChildren()) {
            if (rowNode instanceof HBox row) {
                for (Node child : row.getChildren()) {
                    if (child instanceof TextField tf) {
                        tf.clear();
                        if (firstField == null) {
                            firstField = tf;
                        }
                    }
                }
            }
        }

        if (inputsScroll != null) {
            inputsScroll.setVvalue(0.0);
        }

        if (firstField != null) {
            firstField.requestFocus();
        }

        inputFields.values().forEach(TextField::clear);

        runBtn.setDisable(false);
        stopBtn.setDisable(true);
        resumeBtn.setDisable(true);
        stepOverBtn.setDisable(true);
        radioBtnRegular.setDisable(false);

        mainController.cleanDebugContext();
    }

    public void applyRelevantInputs(Map<String, Long> inputs){
        System.out.println("applyRelevantInputs");
        System.out.println("inputs: " + inputs);
        ObservableList<Node> programInputs = inputsContainer.getChildren();
        for(Node node : programInputs){
            if(node instanceof HBox row){
                ObservableList<Node> rowDetails = row.getChildren();
                if(rowDetails.get(0) instanceof Label label){
                    long value = inputs.getOrDefault(label.getText().substring(0,label.getText().length()-1), 0L);
                    if(rowDetails.get(1) instanceof TextField textField){
                        System.out.println(value);
                        textField.setText(String.valueOf(value));
                        System.out.println(textField.getText());
                    }
                }
            }
        }
    }

}
