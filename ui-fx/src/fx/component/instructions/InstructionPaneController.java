package fx.component.instructions;

import fx.app.util.DisplayUtils;
import fx.app.util.VariableRow;
import fx.system.SEmulatorSystemController;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import semulator.api.dto.*;

import java.util.*;

public class InstructionPaneController {
    private SEmulatorSystemController mainController;
    private ObservableList<InstructionDto> instructionData = FXCollections.observableArrayList();
    private ObservableList<ParentInstructionDto> instructionChainData = FXCollections.observableArrayList();
    private static final String HIGHLIGHT_STYLE_CLASS = "highlight-row";
    private static final String NO_SELECTION_TEXT = "— choose —";

    private static final PseudoClass PC_CURRENT = PseudoClass.getPseudoClass("current");

    // current line in the debug execution (0-based, -1 = none)
    private final IntegerProperty currentLine = new SimpleIntegerProperty(-1);

    private static final PseudoClass PC_CHANGED = PseudoClass.getPseudoClass("changed");
    private final ObservableSet<Long> instructionsToHighlight = FXCollections.observableSet();

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

    private final IntegerProperty basicInstructionsNumber = new SimpleIntegerProperty(0);
    private final IntegerProperty syntheticInstructionsNumber = new SimpleIntegerProperty(0);


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

        lblBasicCount.textProperty().bind(basicInstructionsNumber.asString());
        lblSyntheticCount.textProperty().bind(syntheticInstructionsNumber.asString());

        initCodeTableHighlighting();
        initInstructionsHighlighting();

    }

    public void setMainController(SEmulatorSystemController mainController) {
        this.mainController = mainController;
    }

    public void displayProgram(ProgramFunctionDto programDetails){
        instructionData.clear();
        instructionChainData.clear();

        List<InstructionDto> instructions = programDetails.getInstructions();
        instructionData.addAll(instructions);

        basicInstructionsNumber.set(DisplayUtils.getNumberOfBasicInstructions(programDetails));
        syntheticInstructionsNumber.set(instructions.size() - basicInstructionsNumber.get());

        clearAllRowHighlights();
    }

    private void showParentChain(InstructionDto selected) {
        instructionChainData.clear();
        if (selected == null) {
            return; // nothing selected
        }
        List<ParentInstructionDto> parents = selected.getParents();
        if (parents == null || parents.isEmpty()) {
            // leave table empty; placeholder will be shown
            return;
        }
        instructionChainData.addAll(parents);
    }

    @FXML void onInstructionSelectedListener(MouseEvent event) {
        InstructionDto sel = tblInstructions.getSelectionModel().getSelectedItem();
        showParentChain(sel);
    }



    public void highlightSelectionOnTable(String highlightSelected) {

        List<InstructionDto> tableRows = tblInstructions.getItems();
        Set<Long> instructionsNumbersToHighlight = new HashSet<>();

        if (tableRows == null || tableRows.isEmpty()) {
            return;
        }

        tblInstructions.applyCss();
        tblInstructions.layout();

        clearAllRowHighlights();

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

    private void clearAllRowHighlights() {
        // Make sure rows are realized before lookup
        tblInstructions.applyCss();
        tblInstructions.layout();

        for (Object row : tblInstructions.lookupAll(".table-row-cell")) {
            if (row instanceof TableRow tableRow)
                tableRow.getStyleClass().remove(HIGHLIGHT_STYLE_CLASS);
        }
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

    // call this whenever you move to a new line during debug
    public void highlightLine(int index) {
        Platform.runLater(() -> {
            currentLine.set(index);
            if(index==-1)
                tblInstructions.getSelectionModel().clearSelection();
            else
                tblInstructions.getSelectionModel().clearAndSelect(index);
            tblInstructions.scrollTo(Math.max(index - 3, 0)); // keep line in view
        });
    }


    private void initInstructionsHighlighting() {
        tblInstructions.setRowFactory(tv -> {
            TableRow<InstructionDto> row = new TableRow<>();

            Runnable apply = () -> {
                InstructionDto item = row.getItem();
                boolean highlight = item != null && instructionsToHighlight.contains(item.getNumber());
                row.pseudoClassStateChanged(PC_CHANGED, highlight);
            };

            row.itemProperty().addListener((o, a, b) -> apply.run());
            row.indexProperty().addListener((o, a, b) -> apply.run());
            instructionsToHighlight.addListener((SetChangeListener<Long>) c -> apply.run());

            return row;
        });
    }


    public void markInstructionChanged(Collection<Long> instructionsToMark) {
        Platform.runLater(() -> {
            instructionsToHighlight.clear();
            if (instructionsToMark != null)
                instructionsToHighlight.addAll(instructionsToMark);
            tblInstructions.refresh();

            if (instructionsToMark != null && !instructionsToMark.isEmpty()) {
                Long first = instructionsToMark.iterator().next();
                int idx = -1;
                for (int i = 0; i < tblInstructions.getItems().size(); i++) {
                    if (first.equals(tblInstructions.getItems().get(i).getNumber())) { idx = i; break; }
                }
                if (idx >= 0) tblInstructions.scrollTo(Math.max(idx - 2, 0));
            }
        });

    }
}