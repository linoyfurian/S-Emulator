package fx.app;

import fx.system.SEmulatorSystemController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("S-Emulator");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fx/system/semulatorSystem.fxml"));
        ScrollPane root = loader.load();
        SEmulatorSystemController system = loader.getController();

        Scene scene = new Scene(root, 1100, 680);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
