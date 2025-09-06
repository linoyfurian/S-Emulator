package fx.component.instructions;

import fx.app.util.DisplayUtils;
import fx.system.SEmulatorSystemController;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import semulator.api.dto.InstructionDto;
import semulator.api.dto.ParentInstructionDto;
import semulator.api.dto.ProgramDto;

import java.util.List;

public class InstructionPaneController {
    private SEmulatorSystemController mainController;
    private ObservableList<InstructionDto> instructionData = FXCollections.observableArrayList();
    private ObservableList<ParentInstructionDto> instructionChainData = FXCollections.observableArrayList();

    @FXML private TableView<InstructionDto> tblInstructions;
    @FXML private TableColumn<InstructionDto, String> colNumber;
    @FXML private TableColumn<InstructionDto, String> colType;
    @FXML private TableColumn<InstructionDto, String> colLabel;
    @FXML private TableColumn<InstructionDto, String> colCommand;
    @FXML private TableColumn<InstructionDto, Integer> colCycles;

    @FXML private TableView<ParentInstructionDto> tblChainInstructions;
    @FXML private TableColumn<ParentInstructionDto, String> chainColCommand;
    @FXML private TableColumn<ParentInstructionDto, Integer> chainColCycles;
    @FXML private TableColumn<ParentInstructionDto, String> chainColLabel;
    @FXML private TableColumn<ParentInstructionDto, String> chainColNumber;
    @FXML private TableColumn<ParentInstructionDto, String> chainColType;

    @FXML private Label lblBasicCount;
    @FXML private Label lblSyntheticCount;


    @FXML
    private void initialize() {
        colNumber.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getNumber())));
        colType.setCellValueFactory(cellData ->
                new SimpleStringProperty(String.valueOf(cellData.getValue().getType())));
        colLabel.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getLabel()));
        colCommand.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCommand()));
        colCycles.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getCycles()).asObject());

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
                new SimpleIntegerProperty(cellData.getValue().getCycles()).asObject());

        tblChainInstructions.setItems(instructionChainData);
    }

    public void setMainController(SEmulatorSystemController mainController) {
        this.mainController = mainController;
    }

    public void displayProgram(ProgramDto programDetails){
        int numberOfBasicInstruction, numberOfSyntheticInstruction;
        instructionData.clear();
        List<InstructionDto> instructions = programDetails.getInstructions();
        instructionData.addAll(instructions);

        numberOfBasicInstruction = DisplayUtils.getNumberOfBasicInstructions(programDetails);
        numberOfSyntheticInstruction = instructions.size() -  numberOfBasicInstruction;

        lblBasicCount.setText(String.valueOf(numberOfBasicInstruction));
        lblSyntheticCount.setText(String.valueOf(numberOfSyntheticInstruction));

    }
}
