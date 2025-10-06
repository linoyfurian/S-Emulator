package client.component.execution.instructions;

import client.component.execution.ExecutionController;
import client.utils.display.DisplayUtils;
import dto.InstructionDto;
import dto.ParentInstructionDto;
import dto.ProgramFunctionDto;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

import java.util.List;

public class InstructionsController {
    private ExecutionController mainController;

    private ObservableList<InstructionDto> instructionData = FXCollections.observableArrayList();
    private ObservableList<ParentInstructionDto> instructionChainData = FXCollections.observableArrayList();

    @FXML private TableView<InstructionDto> tblInstructions;
    @FXML private TableColumn<InstructionDto, String> colNumber;
    @FXML private TableColumn<InstructionDto, String> colType;
    @FXML private TableColumn<InstructionDto, String> colLabel;
    @FXML private TableColumn<InstructionDto, String> colCommand;
    @FXML private TableColumn<InstructionDto, String> colCycles;
    @FXML private TableColumn<InstructionDto, Void> colBreakPoint;
    @FXML private TableColumn<InstructionDto, String> colArchitecture;

    @FXML private TableView<ParentInstructionDto> tblChainInstructions;
    @FXML private TableColumn<ParentInstructionDto, String> chainColCommand;
    @FXML private TableColumn<ParentInstructionDto, String> chainColCycles;
    @FXML private TableColumn<ParentInstructionDto, String> chainColLabel;
    @FXML private TableColumn<ParentInstructionDto, String> chainColNumber;
    @FXML private TableColumn<ParentInstructionDto, String> chainColType;
    @FXML private TableColumn<ParentInstructionDto, String> chainColArchitecture;

    @FXML private Label lblBasicCount;
    @FXML private Label lblSyntheticCount;

    private final IntegerProperty basicInstructionsNumber = new SimpleIntegerProperty(0);
    private final IntegerProperty syntheticInstructionsNumber = new SimpleIntegerProperty(0);


    public void setMainController(ExecutionController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void initialize(){
        colNumber.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getNumber())));
        colType.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getType())));
        colLabel.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getLabel()));
        colCommand.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCommand()));
        colCycles.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCycles()));
        colArchitecture.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getArchitecture()));

        tblInstructions.setItems(instructionData);


        chainColNumber.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getNumber())));
        chainColType.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getType())));
        chainColLabel.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getLabel()));
        chainColCommand.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCommand()));
        chainColCycles.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCycles()));
        chainColArchitecture.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getArchitecture()));

        tblChainInstructions.setItems(instructionChainData);

        lblBasicCount.textProperty().bind(basicInstructionsNumber.asString());
        lblSyntheticCount.textProperty().bind(syntheticInstructionsNumber.asString());
    }

    public void displayProgram(ProgramFunctionDto programDetails){
        instructionData.clear();
        instructionChainData.clear();

        List<InstructionDto> instructions = programDetails.getInstructions();
        instructionData.addAll(instructions);

        basicInstructionsNumber.set(DisplayUtils.getNumberOfBasicInstructions(programDetails));
        syntheticInstructionsNumber.set(instructions.size() - basicInstructionsNumber.get());

    }
}
