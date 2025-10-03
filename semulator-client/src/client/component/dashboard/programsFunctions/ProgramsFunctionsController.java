package client.component.dashboard.programsFunctions;

import client.component.dashboard.DashboardController;
import dto.FunctionInfo;
import dto.ProgramInfo;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ProgramsFunctionsController {

    private DashboardController mainController;


    //Available Functions
    @FXML private TableView<FunctionInfo> functionsTbl;
    @FXML private TableColumn<FunctionInfo, Number> functionsTblInstructionsCol;
    @FXML private TableColumn<FunctionInfo, Number> functionsTblMaxDegreeCol;
    @FXML private TableColumn<FunctionInfo, String> functionsTblNameCol;
    @FXML private TableColumn<FunctionInfo, String> functionsTblProgramNameCol;
    @FXML private TableColumn<FunctionInfo, String> functionsTblUserNameCol;

    private ObservableList<FunctionInfo> functionsData = FXCollections.observableArrayList();

    //Available Programs
    @FXML private TableView<ProgramInfo> programsTbl;
    @FXML private TableColumn<ProgramInfo, Number> programsTblAverageCol;
    @FXML private TableColumn<ProgramInfo, Number> programsTblInstructionsCol;
    @FXML private TableColumn<ProgramInfo, Number> programsTblMaxDegreeCol;
    @FXML private TableColumn<ProgramInfo, String> programsTblNameCol;
    @FXML private TableColumn<ProgramInfo, Number> programsTblRunsCol;
    @FXML private TableColumn<ProgramInfo, String> programsTblUserNameCol;

    private ObservableList<ProgramInfo> programsData = FXCollections.observableArrayList();

    public void setMainController(DashboardController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void initialize() {
        functionsTblInstructionsCol.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getInstructionsNumber()));
        functionsTblMaxDegreeCol.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getMaxDegree()));
        functionsTblUserNameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getUserName()));
        functionsTblNameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));
        functionsTblProgramNameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getProgramName()));

        functionsTbl.setItems(functionsData);


        programsTblAverageCol.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getAverageCredits()));
        programsTblInstructionsCol.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getInstructionsNumber()));
        programsTblMaxDegreeCol.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getMaxDegree()));
        programsTblNameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getName()));
        programsTblRunsCol.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getRunsNumber()));
        programsTblUserNameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getUserName()));

        programsTbl.setItems(programsData);
    }


}
