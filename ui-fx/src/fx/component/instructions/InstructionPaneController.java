package fx.component.instructions;

import fx.app.display.Theme;
import fx.app.util.DisplayUtils;
import fx.app.util.VariableRow;
import fx.system.SEmulatorSystemController;
import javafx.application.Platform;
import javafx.beans.property.*;
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
import javafx.scene.layout.VBox;
import semulator.api.dto.*;

import java.util.*;

public class InstructionPaneController {


    private final ToggleGroup breakPoints = new ToggleGroup();           // single selection
    private final IntegerProperty selectedRowIndex = new SimpleIntegerProperty(-1); // -1 = none

    private final BooleanProperty isRunning = new SimpleBooleanProperty(false);


    @FXML private VBox instructionRoot;

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
    @FXML private TableColumn<InstructionDto, String> colCycles;

    @FXML private TableColumn<InstructionDto, Void> colBreakPoint;

    @FXML private TableView<ParentInstructionDto> tblChainInstructions;
    @FXML private TableColumn<ParentInstructionDto, String> chainColCommand;
    @FXML private TableColumn<ParentInstructionDto, String> chainColCycles;
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
                new SimpleStringProperty(cellData.getValue().getCycles()));

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

        tblChainInstructions.setItems(instructionChainData);

        lblBasicCount.textProperty().bind(basicInstructionsNumber.asString());
        lblSyntheticCount.textProperty().bind(syntheticInstructionsNumber.asString());



        initCodeTableHighlighting();
        initInstructionsHighlighting();

        initBreakpointColumn();

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

    public String getInstructionsMainVariable(long currInstructionNumber){
        List<InstructionDto> instructions = this.tblInstructions.getItems();
        InstructionDto currInstruction = instructions.get((int)currInstructionNumber-1);
        if(currInstruction.isJumpInstruction())
            return "";
        return currInstruction.getMainVariable();
    }

    public void onStyleSheetChangedListener(Theme selectedStyleSheet){
        instructionRoot.getStylesheets().clear();
        switch(selectedStyleSheet) {
            case Theme.Default:
                instructionRoot.getStylesheets().add("/fx/component/instructions/instructionsPane.css");
                break;
            case Theme.Dark:
                instructionRoot.getStylesheets().add("/fx/component/instructions/instructionsPaneV2.css");
                break;
            case Theme.Pink:
                instructionRoot.getStylesheets().add("/fx/component/instructions/instructionsPaneV3.css");
                break;
        }
    }


    private void initBreakpointColumn() {
        colBreakPoint.setCellFactory(col -> new TableCell<InstructionDto, Void>() {
            private final RadioButton rb = new RadioButton();

            {
                rb.setFocusTraversable(false);
                rb.setToggleGroup(breakPoints);

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
                       // getTableView().getSelectionModel().select(getIndex());
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

    public int getBreakPointRowIndex() {
        int result;
        if (selectedRowIndex.get() == -1) {
            result = 0;
        }
        else
            result = selectedRowIndex.get() + 1;

        return result;
    }

    public void setIsRunning(boolean isRunning) {
        this.isRunning.set(isRunning);
    }
}