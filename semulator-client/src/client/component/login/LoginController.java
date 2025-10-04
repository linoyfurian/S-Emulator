package client.component.login;

import client.component.dashboard.DashboardController;
import client.utils.Constants;
import client.utils.http.HttpClientUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;

public class LoginController {

    @FXML public TextField userNameTextField;
    @FXML public Label errorMessageLabel;

    private final StringProperty errorMessageProperty = new SimpleStringProperty();

    @FXML
    public void initialize() {
        errorMessageLabel.textProperty().bind(errorMessageProperty);
    }

    @FXML
    private void loginButtonClicked(ActionEvent event) {

        String userName = userNameTextField.getText();
        if (userName.isEmpty()) {
            errorMessageProperty.set("User name is empty. You can't login with empty user name");
            return;
        }

        //noinspection ConstantConditions
        String finalUrl = HttpUrl
                .parse(Constants.LOGIN_PAGE)
                .newBuilder()
                .addQueryParameter("username", userName)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        errorMessageProperty.set("Something went wrong: " + e.getMessage())
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() ->
                            errorMessageProperty.set("Something went wrong: " + responseBody)
                    );
                } else {
                    Platform.runLater(() -> {
                       // onLoginSuccess(userName);
                        errorMessageProperty.set("Login Successful");

                        try{
                            switchToDashBoard(userName);
                        }
                        catch (Exception e){

                        }
                    });
                }
            }
        });
    }


    public void switchToDashBoard(String username) throws Exception{
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(Constants.DASHBOARD_FXML_RESOURCE_LOCATION));
        Parent dashboardRoot = loader.load();

        Stage stage = (Stage) userNameTextField.getScene().getWindow();

        Scene dashboardScene = new Scene(dashboardRoot);
        stage.setScene(dashboardScene);

        stage.setTitle("Dashboard");
        stage.setResizable(true);

        dashboardRoot.applyCss();
        dashboardRoot.autosize();

        stage.setOnCloseRequest(e -> Platform.exit());
        stage.sizeToScene();
        stage.centerOnScreen();
        stage.show();

        DashboardController controller = loader.getController();
        controller.initializeUser(username);
    }
}
