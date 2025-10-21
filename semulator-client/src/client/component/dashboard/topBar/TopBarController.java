package client.component.dashboard.topBar;

import client.component.dashboard.DashboardController;
import client.component.dashboard.topBar.load.ProgressDialog;
import client.utils.Constants;
import client.utils.http.HttpClientUtil;
import com.google.gson.Gson;
import dto.LoadReport;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.FileChooser;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.File;
import java.io.IOException;
import java.util.function.UnaryOperator;

public class TopBarController {
    private DashboardController mainController;


    @FXML private Label userNameLbl;
    @FXML private TextField chargeCreditsTextF;
    @FXML private Label availableCreditsLbl;
    @FXML private TextField fileTextField;

    public void setMainController(DashboardController mainController) {
        this.mainController = mainController;
        availableCreditsLbl.textProperty().bind(mainController.creditsProperty().asString());
        chargeCreditsTextF.setTextFormatter(integerFormatter());
    }

    /** Allow only positive decimal numbers */
    private TextFormatter<String> integerFormatter() {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) return change;
            return null;
        };
        return new TextFormatter<>(filter);
    }


    public void setUserName(String userName) {
        userNameLbl.setText(userName);
    }

    @FXML
    void onLoadFileBtnListener(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select program xml file");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(userNameLbl.getScene().getWindow());
        if (selectedFile == null) return;

        ProgressDialog progress = new ProgressDialog(null, "Loading Program");
        progress.show();
        progress.setStatus("Uploading…");

        HttpClientUtil.postFileAsync(
                Constants.LOAD_FILE_PAGE,
                selectedFile,
                "file",
                null,
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        javafx.application.Platform.runLater(() -> {
                            progress.setStatus("Error: " + e.getMessage());
                            progress.setIndeterminate();
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String body = response.body().string();
                        LoadReport report = new Gson().fromJson(body, LoadReport.class);

                        javafx.application.Platform.runLater(() -> {
                            if (report != null && report.isSuccess()) {
                                progress.setStatus("Loaded successfully.");
                                fileTextField.setText(selectedFile.getAbsolutePath());
                                progress.close();
                            } else {
                                String msg = (report != null ? report.getMessage() : "Invalid server response");
                                progress.setStatus("Error: " + msg);
                                progress.showDetails(msg);
                            }
                        });
                    }
                }
        );
    }

    public String getUserName() {
        return this.userNameLbl.getText();
    }

    @FXML void onChargeCreditsBtnListener(ActionEvent event) {
        String chargeCredits = chargeCreditsTextF.getText();
        if(chargeCredits!=null && !chargeCredits.equals("")) {
            long chargeCreditsNumber = Long.parseLong(chargeCredits);
            this.mainController.addCredits(chargeCreditsNumber);
        }
    }
}
