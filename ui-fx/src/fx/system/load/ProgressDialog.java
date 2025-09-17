package fx.system.load;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ProgressDialog {
    private final Stage dialog;
    private final Label lbl;
    private final ProgressBar bar;
    private final Button btnCancel;

    public ProgressDialog(Stage owner, String title) {
        dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(title);

        lbl = new Label("Loading…");
        bar = new ProgressBar();
        bar.setPrefWidth(260);
        btnCancel = new Button("Cancel");

        VBox root = new VBox(10, lbl, bar, btnCancel);
        root.setPadding(new Insets(12));
        dialog.setScene(new Scene(root));
        dialog.setResizable(false);
    }

    public void bindToTask(Task<?> task) {
        lbl.textProperty().bind(task.messageProperty());
        bar.progressProperty().bind(task.progressProperty());

        // Close-request (X) should cancel the task
        dialog.setOnCloseRequest(ev -> task.cancel());

        // Cancel button cancels the task
        btnCancel.setOnAction(ev -> task.cancel());
    }

    public void show() { dialog.show(); }
    public void close() { dialog.close(); }

    // Optional direct setters (for error flash before closing)
    public void setMessage(String m) { lbl.textProperty().unbind(); lbl.setText(m); }
    public void setIndeterminate() { bar.progressProperty().unbind(); bar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS); }
}
