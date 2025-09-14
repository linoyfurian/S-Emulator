package fx.component.history;

import fx.app.util.ProgramUtil;
import fx.app.util.VariableRow;
import fx.system.SEmulatorSystemController;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import semulator.api.dto.InstructionDto;
import semulator.api.dto.RunResultDto;

import java.util.List;
import java.util.Map;

public class HistoryController {
    private SEmulatorSystemController mainController;

    @FXML private Button btnReRun;
    @FXML private Button btnShowStatus;
    @FXML private Label chosenRunVariablesLbl;
    @FXML private TableView<RunResultDto> historyRunsTable;
    @FXML private TableView<VariableRow> variablesStatusTable;

    @FXML private TableColumn<RunResultDto, Integer> cyclesCol;
    @FXML private TableColumn<RunResultDto, Integer> degreeCol;
    @FXML private TableColumn<RunResultDto, Long> resultYCol;
    @FXML private TableColumn<RunResultDto, Integer> runNumberCol;

    @FXML private TableColumn<VariableRow, String> variableNameCol;
    @FXML private TableColumn<VariableRow, Long> variableValueCol;


    @FXML private VBox vboxVariablesStatus;

    private ObservableList<VariableRow> variablesData = FXCollections.observableArrayList();
    private ObservableList<RunResultDto> historyRunData = FXCollections.observableArrayList();



    public void setMainController(SEmulatorSystemController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void initialize() {
        variableNameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));
        variableValueCol.setCellValueFactory(cellData ->
                new SimpleLongProperty(cellData.getValue().getValue()).asObject());
        variablesStatusTable.setItems(variablesData);

        cyclesCol.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getCycles()).asObject());
        runNumberCol.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getRunNumber()).asObject());
        degreeCol.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getDegreeOfRun()).asObject());
        resultYCol.setCellValueFactory(cellData ->
                new SimpleLongProperty(cellData.getValue().getResultY()).asObject());
        historyRunsTable.setItems(historyRunData);

    }

    public void updateHistoryRunTable(List<RunResultDto> programInContextRunHistory){
        historyRunData.clear();
        historyRunData.addAll(programInContextRunHistory);
        this.vboxVariablesStatus.setVisible(false);
        this.btnShowStatus.setVisible(false);
        this.btnReRun.setVisible(false);
    }

    @FXML void onHistoryRunSelectedListener(MouseEvent event) {
        RunResultDto selectedRun = historyRunsTable.getSelectionModel().getSelectedItem();
        this.btnShowStatus.setVisible(true);
        this.btnReRun.setVisible(true);

    }


    @FXML void onShowStatusListener(ActionEvent event) {
        this.vboxVariablesStatus.setVisible(true);

        RunResultDto selectedRun = historyRunsTable.getSelectionModel().getSelectedItem();
        if(selectedRun != null){
            List<VariableRow> variables = ProgramUtil.generateVariablesRowList(selectedRun);

            this.variablesData.clear();
            this.variablesData.addAll(variables);
        }
    }


    @FXML void onReRunButtonListener(ActionEvent event) {
        RunResultDto selectedRun = historyRunsTable.getSelectionModel().getSelectedItem();
        if(selectedRun != null){
            mainController.onReRunButtonListener(selectedRun);
        }
    }

}
