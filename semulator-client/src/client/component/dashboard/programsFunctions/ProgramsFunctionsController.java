package client.component.dashboard.programsFunctions;

import client.component.dashboard.DashboardController;
import client.component.execution.ExecutionController;
import client.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dto.FunctionInfo;
import dto.ProgramInfo;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class ProgramsFunctionsController {

    private DashboardController mainController;

    @FXML private Button executeProgramBtn;
    @FXML private Button executeFunctionBtn;

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
        this.executeProgramBtn.setDisable(true);

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
                new SimpleDoubleProperty(cellData.getValue().getAverageCredits()));
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

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(2), event -> refreshProgramsAndFunctions())
        );

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        programsTbl.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            executeProgramBtn.setDisable(newSelection == null);
        });

        functionsTbl.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            executeFunctionBtn.setDisable(newSelection == null);
        });
    }


    private void refreshProgramsAndFunctions() {
        new Thread(() -> {
            try {
                URL url = new URL(Constants.PROGRAMS_PAGE);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                try (Scanner scanner = new Scanner(conn.getInputStream())) {
                    StringBuilder json = new StringBuilder();
                    while (scanner.hasNext()) {
                        json.append(scanner.nextLine());
                    }

                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<ProgramInfo>>() {}.getType();
                    List<ProgramInfo> programs = gson.fromJson(json.toString(), listType);

                    List<ProgramInfo> programsDelta = getProgramsDelta(programsData, programs);
                    Platform.runLater(() -> programsData.addAll(programsDelta));
                }

                url = new URL(Constants.FUNCTIONS_PAGE);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                try (Scanner scanner = new Scanner(conn.getInputStream())) {
                    StringBuilder json = new StringBuilder();
                    while (scanner.hasNext()) {
                        json.append(scanner.nextLine());
                    }

                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<FunctionInfo>>() {}.getType();
                    List<FunctionInfo> functions = gson.fromJson(json.toString(), listType);

                    List<FunctionInfo> functionsDelta = getFunctionsDelta(functionsData, functions);
                    Platform.runLater(() -> functionsData.addAll(functionsDelta));
                }


            } catch (Exception e) {
            }
        }).start();
    }

    private List<ProgramInfo> getProgramsDelta(List<ProgramInfo> original, List<ProgramInfo> newPrograms) {
        List<ProgramInfo> programsDelta = new ArrayList<>();
        Set<String> programsSet = new HashSet<>();

        for (ProgramInfo program : original) {
            programsSet.add(program.getName());
        }

        for (ProgramInfo program : newPrograms) {
            if (!programsSet.contains(program.getName())) {
                programsDelta.add(program);
            }
        }
        return programsDelta;
    }

    private List<FunctionInfo> getFunctionsDelta(List<FunctionInfo> original, List<FunctionInfo> newFunctions) {
        List<FunctionInfo> functionsDelta = new ArrayList<>();
        Set<String> functionsSet = new HashSet<>();

        for (FunctionInfo function : original) {
            functionsSet.add(function.getName());
        }

        for (FunctionInfo function : newFunctions) {
            if (!functionsSet.contains(function.getName())) {
                functionsDelta.add(function);
            }
        }
        return functionsDelta;
    }

    @FXML
    void onBtnExecuteProgramListener(ActionEvent event) throws IOException {
        final String FXML_PATH = Constants.EXECUTION_FXML_RESOURCE_LOCATION;

        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH));
        ScrollPane root = loader.load();

        ExecutionController controller = loader.getController();
        controller.setMainController(this.mainController);

        Stage dialog = new Stage();
        dialog.setTitle("Execution");

        String userName = this.mainController.getUserName();
        controller.setUserName(userName);

        ProgramInfo programInContext = this.programsTbl.getSelectionModel().getSelectedItem();
        this.mainController.setExecutionController(controller);
        this.mainController.initialExecutionScreen(programInContext.getName(), true);

        controller.setAverageCredits(programInContext.getAverageCredits());

        Scene scene = new Scene(root);
        dialog.initOwner(this.programsTbl.getScene().getWindow());
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setAlwaysOnTop(true);
        dialog.setScene(scene);
        dialog.showAndWait();
    }


    @FXML
    void onBtnExecuteFunctionListener(ActionEvent event) throws IOException{
        final String FXML_PATH = Constants.EXECUTION_FXML_RESOURCE_LOCATION;

        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH));
        ScrollPane root = loader.load();

        ExecutionController controller = loader.getController();
        controller.setMainController(this.mainController);

        Stage dialog = new Stage();
        dialog.setTitle("Execution");

        String userName = this.mainController.getUserName();
        controller.setUserName(userName);

        FunctionInfo programInContext = this.functionsTbl.getSelectionModel().getSelectedItem();
        this.mainController.setExecutionController(controller);
        this.mainController.initialExecutionScreen(programInContext.getName(), false);

        Scene scene = new Scene(root);
        dialog.initOwner(this.programsTbl.getScene().getWindow());
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setAlwaysOnTop(true);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

}
