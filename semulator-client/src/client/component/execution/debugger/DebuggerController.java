package client.component.execution.debugger;

import client.component.execution.ExecutionController;
import client.utils.display.VariableRow;
import dto.ProgramFunctionDto;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

public class DebuggerController {
    private ExecutionController mainController;

    private final String INPUT_VARIABLES = "input-variable";
    private final Map<String, TextField> inputFields = new HashMap<>();

    private final ObservableSet<String> changedVarNames = FXCollections.observableSet();

    @FXML
    private ScrollPane inputsScroll;
    @FXML
    private VBox inputsContainer;

    private ObservableList<VariableRow> variablesData = FXCollections.observableArrayList();

    @FXML
    private TableColumn<VariableRow, String> nameCol;
    @FXML
    private TableColumn<VariableRow, String> valueCol;
    @FXML
    private TableView<VariableRow> variablesTable;

    @FXML
    private Label cyclesLabel;
    private IntegerProperty cyclesProperty = new SimpleIntegerProperty(0);

    @FXML
    private Button runBtn;
    @FXML
    private RadioButton radioBtnDebug;
    @FXML
    private RadioButton radioBtnRegular;
    @FXML
    private Button resumeBtn;
    @FXML
    private Button stepOverBtn;
    @FXML
    private Button stopBtn;
    @FXML
    private Button stepBackBtn;

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
        runBtn.setDisable(false);
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
}
