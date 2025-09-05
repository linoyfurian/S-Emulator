package fx.component.instructions;

import fx.system.SEmulatorSystemController;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import semulator.api.dto.InstructionDto;
import semulator.api.dto.ProgramDto;

import java.util.List;

public class InstructionPaneController {
    private SEmulatorSystemController mainController;
    private ObservableList<InstructionDto> instructionData = FXCollections.observableArrayList();

    @FXML
    private TableView<InstructionDto> tblInstructions;
    @FXML private TableColumn<InstructionDto, String> colNumber;
    @FXML private TableColumn<InstructionDto, String> colType;
    @FXML private TableColumn<InstructionDto, String> colLabel;
    @FXML private TableColumn<InstructionDto, String> colCommand;
    @FXML private TableColumn<InstructionDto, Integer> colCycles;


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
    }

    public void setMainController(SEmulatorSystemController mainController) {
        this.mainController = mainController;
    }

    public void displayProgram(ProgramDto programDetails){
        List<InstructionDto> instructions = programDetails.getInstructions();
        instructionData.addAll(instructions);
    }
}
