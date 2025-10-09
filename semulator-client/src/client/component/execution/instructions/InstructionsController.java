package client.component.execution.instructions;

import client.component.execution.ExecutionController;
import client.utils.display.DisplayUtils;
import client.utils.display.ProgramUtil;
import dto.InstructionDto;
import dto.ParentInstructionDto;
import dto.ProgramFunctionDto;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InstructionsController {
    private ExecutionController mainController;

    private final ObservableSet<Long> instructionsToHighlight = FXCollections.observableSet();
    private final ObservableSet<Long> validInstructions = FXCollections.observableSet();
    private final ObservableSet<Long> invalidInstructions = FXCollections.observableSet();


    private final ToggleGroup breakPoints = new ToggleGroup(); // single selection
    private final IntegerProperty selectedRowIndex = new SimpleIntegerProperty(-1); // -1 = none

    private final BooleanProperty isRunning = new SimpleBooleanProperty(false);


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
    @FXML private Label lblArchitectureICount;
    @FXML private Label lblArchitectureIICount;
    @FXML private Label lblArchitectureIIICount;
    @FXML private Label lblArchitectureIVCount;

    private final IntegerProperty basicInstructionsNumber = new SimpleIntegerProperty(0);
    private final IntegerProperty syntheticInstructionsNumber = new SimpleIntegerProperty(0);
    private final IntegerProperty architectureINumber = new SimpleIntegerProperty(0);
    private final IntegerProperty architectureIINumber = new SimpleIntegerProperty(0);
    private final IntegerProperty architectureIIINumber = new SimpleIntegerProperty(0);
    private final IntegerProperty architectureIVNumber = new SimpleIntegerProperty(0);

    private static final PseudoClass PC_CURRENT = PseudoClass.getPseudoClass("current");
    // current line in the debug execution (0-based, -1 = none)
    private final IntegerProperty currentLine = new SimpleIntegerProperty(-1);
    private static final PseudoClass PC_CHANGED = PseudoClass.getPseudoClass("changed");

    private static final PseudoClass PC_VALID = PseudoClass.getPseudoClass("valid");
    private static final PseudoClass PC_INVALID = PseudoClass.getPseudoClass("invalid");

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

        lblArchitectureICount.textProperty().bind(architectureINumber.asString());
        lblArchitectureIICount.textProperty().bind(architectureIINumber.asString());
        lblArchitectureIIICount.textProperty().bind(architectureIIINumber.asString());
        lblArchitectureIVCount.textProperty().bind(architectureIVNumber.asString());


        initCodeTableHighlighting();
       // initInstructionsHighlighting();

        initInstructionsArchitectureHighlighting();

        initBreakpointColumn();
    }

    public void displayProgram(ProgramFunctionDto programDetails){
        instructionData.clear();
        instructionChainData.clear();

        List<InstructionDto> instructions = programDetails.getInstructions();
        instructionData.addAll(instructions);

        basicInstructionsNumber.set(DisplayUtils.getNumberOfBasicInstructions(programDetails));
        syntheticInstructionsNumber.set(instructions.size() - basicInstructionsNumber.get());

        architectureINumber.set(DisplayUtils.getNumberOfArchitectureX(instructions,"I"));
        architectureIINumber.set(DisplayUtils.getNumberOfArchitectureX(instructions, "II"));
        architectureIIINumber.set(DisplayUtils.getNumberOfArchitectureX(instructions, "III"));
        architectureIVNumber.set(DisplayUtils.getNumberOfArchitectureX(instructions, "IV"));

    }

    public void resetBreakPointSelection(){
        selectedRowIndex.set(-1);
    }


    public void highlightSelectionOnTable(String highlightSelected) {

        List<InstructionDto> tableRows = tblInstructions.getItems();
        Set<Long> instructionsNumbersToHighlight = new HashSet<>();

        if (tableRows == null || tableRows.isEmpty()) {
            return;
        }

        tblInstructions.applyCss();
        tblInstructions.layout();

       // clearAllRowHighlights();

        boolean isInstructionToHighlight;
        for (InstructionDto instruction : tableRows) {
            isInstructionToHighlight = isInstructionContainsHighlightSelected(instruction, highlightSelected);
            if (isInstructionToHighlight) {
                instructionsNumbersToHighlight.add(instruction.getNumber());
            }
        }
        tblInstructions.refresh();
        tblInstructions.applyCss();
        tblInstructions.layout();

        markInstructionChanged(instructionsNumbersToHighlight);
    }

    private boolean isInstructionContainsHighlightSelected(InstructionDto instructionToCheck, String highlightSelected) {
        boolean result = false;
        List<String> allVariables = instructionToCheck.getAllVariables();
        List<String> allLabels = instructionToCheck.getAllLabels();

        for (String variable : allVariables) {
            if(variable.equals(highlightSelected)) {
                if(!(instructionToCheck.getCommand().startsWith("GOTO"))) {
                    result = true;
                    break;
                }
            }
        }

        for (String label : allLabels) {
            if(label.equals(highlightSelected)) {
                result = true;
                break;
            }
        }
        return result;
    }

    public void markInstructionChanged(Collection<Long> instructionsToMark) {
        Platform.runLater(() -> {
            instructionsToHighlight.clear();
            if (instructionsToMark != null)
                instructionsToHighlight.addAll(instructionsToMark);
            tblInstructions.refresh();
        });

    }

    private void initCodeTableHighlighting() {
        tblInstructions.setRowFactory(tv -> {
            TableRow<InstructionDto> row = new TableRow<>();

            Runnable apply = () -> {
                boolean highlight = row.getIndex() == currentLine.get();
                row.pseudoClassStateChanged(PC_CURRENT, highlight);
            };

            currentLine.addListener((obs, oldV, newV) -> apply.run());
            row.indexProperty().addListener((obs, oldV, newV) -> apply.run());
            row.itemProperty().addListener((obs, oldV, newV) -> apply.run());

            return row;
        });
    }
    // highlight a new line during debug
    public void highlightLine(int index) {
        currentLine.set(index);
        initInstructionsArchitectureHighlighting();
    }

    private void initBreakpointColumn() {
        colBreakPoint.setCellFactory(col -> new TableCell<InstructionDto, Void>() {
            private final RadioButton rb = new RadioButton();

            {
                rb.setFocusTraversable(false);
                rb.setToggleGroup(breakPoints);

                rb.getStyleClass().add("breakpoint-radio-button");
                rb.addEventFilter(MouseEvent.MOUSE_PRESSED, ev -> {
                    if (isRunning.get()) {
                        ev.consume();
                        return;
                    }
                    if (rb.isSelected()) {
                        rb.setSelected(false);
                        ev.consume();
                    }
                });

                // When user clicks this radio, remember the current row index
                rb.setOnAction(e -> {
                    if (rb.isSelected()) {
                        selectedRowIndex.set(getIndex());
                    } else if (selectedRowIndex.get() == getIndex()) {
                        selectedRowIndex.set(-1);
                    }

                    getTableView().getSelectionModel().clearSelection();
                });

                rb.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                    if (rb.isSelected()) {
                        rb.setSelected(false);
                        event.consume();
                    }
                });

                // When the "global" selectedRowIndex changes, refresh this cell's checked state
                selectedRowIndex.addListener((obs, oldV, newV) -> {
                    // Cells are virtualized; just reflect whether our index is selected
                    rb.setSelected(getIndex() == newV.intValue());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    rb.setToggleGroup(null); // detach when not used
                } else {
                    // re-attach for visible cell (virtualization-safe)
                    if (rb.getToggleGroup() == null) {
                        rb.setToggleGroup(breakPoints);
                    }
                    rb.setSelected(getIndex() == selectedRowIndex.get());
                    setGraphic(rb);
                    rb.disableProperty().bind(isRunning);
                }
            }
        });
    }

    private void showParentChain(InstructionDto selected) {
        instructionChainData.clear();
        if (selected == null) {
            return; // nothing selected
        }
        List<ParentInstructionDto> parents = selected.getParents();
        if (parents == null || parents.isEmpty()) {
            return;
        }
        instructionChainData.addAll(parents);
    }

    @FXML void onInstructionSelectedListener(MouseEvent event) {
        InstructionDto sel = tblInstructions.getSelectionModel().getSelectedItem();
        showParentChain(sel);
    }

    public boolean checkIfRunIsValid(String selectedArchitecture){
        if(selectedArchitecture.equals("IV"))
            return true;
        if(selectedArchitecture.equals("III")){
            if(architectureIVNumber.get()==0)
                return true;
            else
                return false;
        }
        if(selectedArchitecture.equals("II")){
            if(architectureIVNumber.get()==0 &&architectureIIINumber.get()==0)
                return true;
            else
                return false;
        }
        if(selectedArchitecture.equals("I")){
            if(architectureIVNumber.get()==0 && architectureIIINumber.get()==0 && architectureIINumber.get()==0)
                return true;
            else
                return false;
        }
            return false;
    }

    public void highlightByArchitecture(String selectedArchitecture) {
        int selectedLevel = ProgramUtil.getArchitectureLevel(selectedArchitecture);
        String currentArchitecture;
        int currentLevel;

        this.validInstructions.clear();
        this.invalidInstructions.clear();


        for(InstructionDto instruction : instructionData){
            currentArchitecture = instruction.getArchitecture();
            currentLevel = ProgramUtil.getArchitectureLevel(currentArchitecture);
            if(currentLevel <= selectedLevel){
                this.validInstructions.add(instruction.getNumber());
            }
            else
                this.invalidInstructions.add(instruction.getNumber());
        }

        tblInstructions.refresh();

    }

    private void initInstructionsArchitectureHighlighting() {
        tblInstructions.setRowFactory(tv -> {
            TableRow<InstructionDto> row = new TableRow<>();

            Runnable apply = () -> {
                InstructionDto item = row.getItem();
                boolean isValid = item != null && validInstructions.contains(item.getNumber());
                boolean debugHighlight = item != null && row.getIndex() == currentLine.get();
                boolean isInvalid = item != null && invalidInstructions.contains(item.getNumber());
                boolean highlight = item != null && instructionsToHighlight.contains(item.getNumber());

                if(debugHighlight){
                    row.pseudoClassStateChanged(PC_CURRENT, true);
                    row.pseudoClassStateChanged(PC_CHANGED, false);
                    row.pseudoClassStateChanged(PC_VALID, false);
                    row.pseudoClassStateChanged(PC_INVALID, false);
                }
                else if (highlight) {
                    row.pseudoClassStateChanged(PC_CHANGED, true);
                    row.pseudoClassStateChanged(PC_CURRENT, false);
                    row.pseudoClassStateChanged(PC_VALID, false);
                    row.pseudoClassStateChanged(PC_INVALID, false);
                } else {
                    row.pseudoClassStateChanged(PC_CHANGED, false);
                    row.pseudoClassStateChanged(PC_CURRENT, false);
                    row.pseudoClassStateChanged(PC_VALID, isValid);
                    row.pseudoClassStateChanged(PC_INVALID, isInvalid);
                }
            };

            row.itemProperty().addListener((o, a, b) -> apply.run());
            row.indexProperty().addListener((o, a, b) -> apply.run());
            validInstructions.addListener((SetChangeListener<Long>) c -> apply.run());

            return row;
        });

    }

    public void setIsRunning(boolean isRunning) {
        this.isRunning.set(isRunning);
    }
}