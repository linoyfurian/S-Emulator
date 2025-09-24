package fx.system.create;

import fx.system.SEmulatorSystemController;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import semulator.api.dto.InstructionDraft;
import semulator.api.dto.ProgramDraft;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class AddProgramController {
    private SEmulatorSystemController mainController;

    @FXML private Label mainVariableIndexLbl;
    @FXML private Label mainLabelIndexLbl;
    @FXML private Label additionalVariableIndexLbl;
    @FXML private Label additionalLabelIndexLbl;

    @FXML private Button saveToFileBtn;

    @FXML private Button deleteInstructionBtn;
    @FXML private Button addInstructionButton;
    @FXML private Label additionalArgsLbl;
    @FXML private HBox additionalArgsRow;
    @FXML private ChoiceBox<String> additionalLabelCbox;
    @FXML private TextField additionalLabelTF;
    @FXML private TextField   constantValueTF;
    @FXML private ChoiceBox<String> additionalVariableCbox;
    @FXML private TextField additionalVariableTF;
    @FXML private ComboBox<String> instructionNameCbox;
    @FXML private ChoiceBox<String> labelCbox;
    @FXML private TextField mainLabelTF;
    @FXML private TextField mainVariableTF;
    @FXML private ChoiceBox<String> variableCbox;
    @FXML private TextField programNameTF;
    @FXML private VBox additionalVariableVbox;
    @FXML private VBox additionalLabelVbox;
    @FXML private VBox additionalConstantVbox;
    @FXML private Button uploadProgramBtn;

    @FXML private Label mainVariableLbl;
    @FXML private Label mainLabelLbl;
    @FXML private Label additionalVariableLbl;
    @FXML private Label additionalLabelLbl;

    private final BooleanProperty canAddInstruction = new SimpleBooleanProperty(false);

    private List<InstructionDraft> newProgramInstructions = new ArrayList<>();


    @FXML private TableView<InstructionDraft> newInstructionTbl;
    @FXML private TableColumn<InstructionDraft, Integer> colCycles;
    @FXML private TableColumn<InstructionDraft, String> colLabel;
    @FXML private TableColumn<InstructionDraft, String> colName;
    @FXML private TableColumn<InstructionDraft, String> colType;
    @FXML private TableColumn<InstructionDraft, Integer>  colNumber;
    private ObservableList<InstructionDraft> instructionData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        this.instructionNameCbox.getItems().addAll(CreateProgramUtils.getInstructionsNameOptions());
        this.additionalLabelCbox.getItems().addAll(CreateProgramUtils.getLabelTypeOptions());
        this.labelCbox.getItems().addAll(CreateProgramUtils.getLabelTypeOptionsPlus());
        this.variableCbox.getItems().addAll(CreateProgramUtils.getVariablesTypeOptions());
        this.additionalVariableCbox.getItems().addAll(CreateProgramUtils.getVariablesTypeOptions());

        this.additionalVariableTF.setTextFormatter(integerFormatter());
        this.additionalLabelTF.setTextFormatter(integerFormatter());
        this.mainLabelTF.setTextFormatter(integerFormatter());
        this.mainVariableTF.setTextFormatter(integerFormatter());
        this.constantValueTF.setTextFormatter(integerFormatter());


        this.mainVariableTF.setVisible(false);
        this.mainLabelTF.setVisible(false);
        this.mainVariableIndexLbl.setVisible(false);

        this.mainLabelIndexLbl.setVisible(false);

        this.additionalLabelTF.setVisible(false);
        this.additionalLabelCbox.setVisible(false);
        this.additionalLabelLbl.setVisible(false);
        this.additionalLabelIndexLbl.setVisible(false);

        this.additionalVariableTF.setVisible(false);
        this.additionalVariableCbox.setVisible(false);
        this.additionalVariableLbl.setVisible(false);
        this.additionalVariableIndexLbl.setVisible(false);

        this.additionalArgsLbl.setVisible(false);



        BooleanBinding addDisabled = Bindings.createBooleanBinding(
                () -> !isInstructionReadyToAdd(),
                instructionNameCbox.valueProperty(),
                variableCbox.valueProperty(),
                additionalVariableCbox.valueProperty(),
                additionalLabelCbox.valueProperty(),
                mainVariableTF.textProperty(),
                additionalVariableTF.textProperty(),
                additionalLabelTF.textProperty(),
                constantValueTF.textProperty()
        );
        addInstructionButton.disableProperty().bind(addDisabled);

        updateExtraSections(instructionNameCbox.getValue());

        instructionData.addAll(newProgramInstructions);

        colName.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getCommand()));
        colType.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getType()));
        colLabel.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getMainLabel()));
        colCycles.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(cellData.getValue().getCycles()).asObject());
        colNumber.setCellValueFactory(cellData ->
                        new ReadOnlyObjectWrapper<>(newInstructionTbl.getItems().indexOf(cellData.getValue()) + 1));

        newInstructionTbl.setItems(instructionData);

        this.uploadProgramBtn.setDisable(true);
        this.saveToFileBtn.setDisable(true);

    }

    public void setMainController(SEmulatorSystemController mainController) {
        this.mainController = mainController;
    }

    private TextFormatter<String> integerFormatter() {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) return change;
            return null;
        };
        return new TextFormatter<>(filter);
    }

    private void updateExtraSections(String selectedInstructionName) {
        boolean isAdditionalLabel = false;
        boolean isAdditionalVariable = false;
        boolean isAdditionalConstant = false;

        this.variableCbox.setVisible(true);
        this.mainVariableLbl.setVisible(true);


        if (selectedInstructionName != null) {
            switch(selectedInstructionName){
                case "ASSIGNMENT":
                    isAdditionalVariable = true;
                    break;
                case "CONSTANT_ASSIGNMENT":
                    isAdditionalConstant = true;
                    break;
                case "JUMP_NOT_ZERO":
                    isAdditionalLabel = true;
                    break;
                case"JUMP_ZERO":
                    isAdditionalLabel = true;
                    break;
                case "JUMP_EQUAL_VARIABLE":
                    isAdditionalVariable = true;
                    isAdditionalLabel = true;
                    break;
                case "JUMP_EQUAL_CONSTANT":
                    isAdditionalConstant = true;
                    isAdditionalLabel = true;
                    break;
                case "GOTO_LABEL":
                    isAdditionalLabel = true;
                    this.variableCbox.setVisible(false);
                    this.mainVariableLbl.setVisible(false);
                    this.mainVariableIndexLbl.setVisible(false);
                    break;
            }
        }

        if(isAdditionalVariable||isAdditionalLabel||isAdditionalConstant){
            this.additionalArgsLbl.setVisible(true);
        }

        if(isAdditionalVariable) {
            this.additionalVariableCbox.setVisible(true);
            this.additionalVariableLbl.setVisible(true);
        }
        else {
            this.additionalVariableCbox.setVisible(false);
            this.additionalVariableLbl.setVisible(false);
        }
        if(isAdditionalConstant)
            this.additionalConstantVbox.setVisible(true);
        else
            this.additionalConstantVbox.setVisible(false);
        if(isAdditionalLabel) {
            this.additionalLabelCbox.setVisible(true);
            this.additionalLabelLbl.setVisible(true);
        }
        else {
            this.additionalLabelCbox.setVisible(false);
            this.additionalLabelLbl.setVisible(false);
        }

        resetAllOptions();
    }

    @FXML void onSelectedInstructionName(ActionEvent event) {
        updateExtraSections(instructionNameCbox.getValue());
    }

    @FXML void onBtnAddInstructionListener(ActionEvent event) {
        InstructionDraft newInstruction;
        String instructionName, mainVariable, additionalVariable, mainLabel, additionalLabel, constantValueStr;
        long constantValue;

        instructionName = instructionNameCbox.getSelectionModel().getSelectedItem();
        mainVariable = variableCbox.getSelectionModel().getSelectedItem();
        if(mainVariable == null){
            mainVariable = "";
        }
        else if(!mainVariable.equals("y"))
                  mainVariable = mainVariable + mainVariableTF.getText();

        mainLabel = labelCbox.getSelectionModel().getSelectedItem();

        if(mainLabel == null||mainLabel.equals("no label"))
            mainLabel = "";
        else if(mainLabel.equals("L")){
            mainLabel = mainLabel + mainLabelTF.getText();
        }

        additionalVariable = additionalVariableCbox.getSelectionModel().getSelectedItem();
        if(additionalVariable == null)
            additionalVariable = "";
        else if(!additionalVariable.equals("y"))
            additionalVariable = additionalVariable + additionalVariableTF.getText();

        additionalLabel = additionalLabelCbox.getSelectionModel().getSelectedItem();
        if(additionalLabel == null)
            additionalLabel = "";
        else if(additionalLabel.equals("L"))
            additionalLabel = additionalLabel + additionalLabelTF.getText();


        constantValueStr = constantValueTF.getText();
        if(constantValueStr.equals("")||constantValueStr==null)
            constantValue = 0L;
        else
            constantValue = Long.parseLong(constantValueStr);

        newInstruction = new InstructionDraft(instructionName, mainVariable, mainLabel, constantValue, additionalVariable, additionalLabel);

        this.instructionData.add(newInstruction);

        this.uploadProgramBtn.setDisable(false);
        this.saveToFileBtn.setDisable(false);

        this.instructionNameCbox.getSelectionModel().clearSelection();
        resetAllOptions();
    }

    private void resetAllOptions(){
        this.variableCbox.getSelectionModel().clearSelection();
        this.additionalVariableCbox.getSelectionModel().clearSelection();
        this.additionalLabelCbox.getSelectionModel().clearSelection();
        this.labelCbox.getSelectionModel().clearSelection();
        this.constantValueTF.clear();
        this.additionalVariableTF.clear();
        this.additionalLabelTF.clear();
        this.mainLabelTF.clear();
        this.mainVariableTF.clear();

        this.mainVariableTF.setVisible(false);
        this.mainLabelTF.setVisible(false);
        this.mainVariableTF.setVisible(false);
        this.mainVariableIndexLbl.setVisible(false);
        this.mainLabelIndexLbl.setVisible(false);


        this.additionalLabelTF.setVisible(false);
        this.additionalLabelCbox.setVisible(false);
        this.additionalLabelLbl.setVisible(false);
        this.additionalLabelIndexLbl.setVisible(false);

        this.additionalVariableTF.setVisible(false);
        this.additionalVariableCbox.setVisible(false);
        this.additionalVariableLbl.setVisible(false);
        this.additionalVariableIndexLbl.setVisible(false);

        this.additionalArgsLbl.setVisible(false);
    }


    private boolean isInstructionReadyToAdd(){
        String selectedLabelType;

        if(instructionNameCbox.getSelectionModel().getSelectedItem()==null){
            return false;
        }

        String currentInstructionName = instructionNameCbox.getSelectionModel().getSelectedItem();

        if(variableCbox.getSelectionModel().getSelectedItem()==null && (!(currentInstructionName.equals("GOTO_LABEL"))))
            return false;
        else{
            if(!currentInstructionName.equals("GOTO_LABEL")){
                String selectedVariableType = variableCbox.getSelectionModel().getSelectedItem();
                if (!selectedVariableType.equals("y")) {
                    if ((mainVariableTF.getText().isEmpty() || mainVariableTF.getText() == null)) {
                        return false;
                    }
                }
            }
        }

        String selectedVariableType;
        switch(currentInstructionName){
            case "ASSIGNMENT":
                if(additionalVariableCbox.getSelectionModel().getSelectedItem()==null)
                    return false;
                selectedVariableType = additionalVariableCbox.getSelectionModel().getSelectedItem();
                if (!selectedVariableType.equals("y")) {
                    if ((additionalVariableTF.getText().isEmpty() || additionalVariableTF.getText() == null)) {
                        return false;
                    }
                }
                break;
            case "CONSTANT_ASSIGNMENT":
                if(constantValueTF.getText().isEmpty()|| constantValueTF.getText()==null)
                    return false;
                break;
            case "JUMP_NOT_ZERO":
                selectedLabelType = additionalLabelCbox.getSelectionModel().getSelectedItem();
                if(selectedLabelType == null){
                    return false;
                }
                else if(selectedLabelType.equals("L")){
                    if(additionalLabelTF.getText().isEmpty()|| additionalLabelTF.getText()==null)
                        return false;
                }
                break;
            case "JUMP_ZERO":
                selectedLabelType = additionalLabelCbox.getSelectionModel().getSelectedItem();
                if(selectedLabelType == null){
                    return false;
                }
                else if(selectedLabelType.equals("L")){
                    if(additionalLabelTF.getText().isEmpty()|| additionalLabelTF.getText()==null)
                        return false;
                }
                break;
            case "JUMP_EQUAL_VARIABLE":
                selectedLabelType = additionalLabelCbox.getSelectionModel().getSelectedItem();
                if(selectedLabelType == null){
                    return false;
                }
                else if(selectedLabelType.equals("L")){
                    if(additionalLabelTF.getText().isEmpty()|| additionalLabelTF.getText()==null)
                        return false;
                }

                if(additionalVariableCbox.getSelectionModel().getSelectedItem()==null)
                    return false;
                selectedVariableType = additionalVariableCbox.getSelectionModel().getSelectedItem();
                if (!selectedVariableType.equals("y")) {
                    if ((additionalVariableTF.getText().isEmpty() || additionalVariableTF.getText() == null)) {
                        return false;
                    }
                }
                break;
            case "GOTO_LABEL":
                selectedLabelType = additionalLabelCbox.getSelectionModel().getSelectedItem();
                if(selectedLabelType == null){
                    return false;
                }
                else if(selectedLabelType.equals("L")){
                    if(additionalLabelTF.getText().isEmpty()|| additionalLabelTF.getText()==null)
                        return false;
                }
                break;
            case "JUMP_EQUAL_CONSTANT":
                selectedLabelType = additionalLabelCbox.getSelectionModel().getSelectedItem();
                if(selectedLabelType == null){
                    return false;
                }
                else if(selectedLabelType.equals("L")){
                    if(additionalLabelTF.getText().isEmpty()|| additionalLabelTF.getText()==null)
                        return false;
                }
                if(constantValueTF.getText().isEmpty()|| constantValueTF.getText()==null)
                    return false;
                break;
        }
        return true;
    }


    @FXML void onBtnUploadProgramListenter(ActionEvent event) {
        ProgramDraft newProgram;
        String programName;

        programName = this.programNameTF.getText();
        if(programName.isEmpty()||programName==null){
            programName = "";
        }

        newProgram = new ProgramDraft(programName, this.instructionData);
        mainController.onBtnUploadProgramListenter(newProgram);
        Stage stage = (Stage) programNameTF.getScene().getWindow();
        stage.close();
    }

    @FXML void onVariableTypeCboxSelected(ActionEvent event) {
        String selectedVariableType = variableCbox.getSelectionModel().getSelectedItem();
        if(selectedVariableType==null){
            return;
        }
        if(!selectedVariableType.equals("y")){
            this.mainVariableTF.setVisible(true);
            this.mainVariableIndexLbl.setVisible(true);
        }
        else {
            this.mainVariableTF.setVisible(false);
            this.mainVariableIndexLbl.setVisible(false);
        }
    }


    @FXML void onLabelCboxSelected(ActionEvent event) {
        String selectedLabelType = this.labelCbox.getSelectionModel().getSelectedItem();
        if(selectedLabelType==null){
            return;
        }
        if(selectedLabelType.equals("L")){
            this.mainLabelTF.setVisible(true);
            this.mainLabelIndexLbl.setVisible(true);
        }
        else {
            this.mainLabelTF.setVisible(false);
            this.mainLabelIndexLbl.setVisible(false);
        }
    }

    @FXML void onAdditionalVariableTypeCboxSelected(ActionEvent event) {
        String selectedVariableType = additionalVariableCbox.getSelectionModel().getSelectedItem();
        if(selectedVariableType==null){
            return;
        }
        if(!selectedVariableType.equals("y")){
            this.additionalVariableTF.setVisible(true);
            this.additionalVariableIndexLbl.setVisible(true);
        }
        else {
            this.additionalVariableTF.setVisible(false);
            this.additionalVariableIndexLbl.setVisible(false);
        }
    }


    @FXML
    void onAdditionalLabelCboxSelected(ActionEvent event) {
        String selectedLabelType = this.additionalLabelCbox.getSelectionModel().getSelectedItem();
        if(selectedLabelType==null){
            return;
        }
        if(selectedLabelType.equals("L")){
            this.additionalLabelTF.setVisible(true);
            this.additionalLabelIndexLbl.setVisible(true);
        }
        else {
            this.additionalLabelTF.setVisible(false);
            this.additionalLabelIndexLbl.setVisible(false);
        }
    }


    @FXML
    void onInstructionSelectedListener(MouseEvent event) {
        this.deleteInstructionBtn.setVisible(true);
    }

    @FXML
    void onBtnDeleteInstructionListener(ActionEvent event) {
        this.instructionData.remove(this.newInstructionTbl.getSelectionModel().getSelectedItem());
        this.deleteInstructionBtn.setVisible(false);
    }

    @FXML void onSaveToFileBtnListener(ActionEvent event) {
        ProgramDraft newProgram;
        String programName;

        programName = this.programNameTF.getText();
        if (programName.isEmpty() || programName == null) {
            programName = "";
        }

        newProgram = new ProgramDraft(programName, this.instructionData);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Program As...");

        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        fileChooser.setInitialFileName("new-created-program.xml");

        File file = fileChooser.showSaveDialog(saveToFileBtn.getScene().getWindow());
        if (file != null) {
            try {
                mainController.saveProgramToFile(newProgram, file);
            } catch (Exception e) {
            }
        }
    }
}


