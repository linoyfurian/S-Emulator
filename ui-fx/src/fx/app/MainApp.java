package fx.app;

import fx.system.SEmulatorSystemController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("S-Emulator");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fx/system/semulatorSystem.fxml"));
        BorderPane root = loader.load();                       // טוענים דרך ה-loader
        SEmulatorSystemController system = loader.getController();

        // אם צריך להזרים תלותים לתוך ה-root (לא חובה אם ה-root יוצר engine בעצמו)
        // system.setEngine(new SEmulatorEngineImpl());

        Scene scene = new Scene(root, 1100, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
