package client.component.execution.instructions;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

public class InstructionsController {

    @FXML private TableView<?> tblChainInstructions;
    @FXML private TableColumn<?, ?> chainColArchitecture;
    @FXML private TableColumn<?, ?> chainColCommand;
    @FXML private TableColumn<?, ?> chainColCycles;
    @FXML private TableColumn<?, ?> chainColLabel;
    @FXML private TableColumn<?, ?> chainColNumber;
    @FXML private TableColumn<?, ?> chainColType;

    @FXML private TableView<?> tblInstructions;
    @FXML private TableColumn<?, ?> colArchitecture;
    @FXML private TableColumn<?, ?> colBreakPoint;
    @FXML private TableColumn<?, ?> colCommand;
    @FXML private TableColumn<?, ?> colCycles;
    @FXML private TableColumn<?, ?> colLabel;
    @FXML private TableColumn<?, ?> colNumber;
    @FXML private TableColumn<?, ?> colType;

    @FXML private VBox instructionRoot;
    @FXML private Label lblBasicCount;
    @FXML private Label lblSyntheticCount;

}
