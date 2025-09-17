package fx.system.load;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ProgressDialog {
    private final Stage dialog;
    private final Label lblStatus;       // "Loading..."/"Canceling..."
    private final ProgressBar pb;
    private final TextArea taDetails;    // hidden at start
    private final Button btnCancel;

    public ProgressDialog(Stage owner, String title) {
        dialog = new Stage();
        if (owner != null) dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(title);

        lblStatus = new Label("Loading…");
        lblStatus.setWrapText(true);

        pb = new ProgressBar();
        pb.setPrefWidth(320);
        pb.setMaxWidth(Double.MAX_VALUE);

        taDetails = new TextArea();
        taDetails.setWrapText(true);
        taDetails.setEditable(false);
        taDetails.setPrefRowCount(4);
        taDetails.setMaxWidth(Double.MAX_VALUE);

        taDetails.setVisible(false);
        taDetails.setManaged(false);

        btnCancel = new Button("Cancel");

        VBox root = new VBox(10, lblStatus, pb, taDetails, btnCancel);
        root.setPadding(new Insets(12));
        root.setFillWidth(true);

        root.setPrefWidth(420);
        root.setPrefHeight(150);

        lblStatus.setMaxWidth(Double.MAX_VALUE);
        lblStatus.prefWidthProperty().bind(root.widthProperty().subtract(24));

        dialog.setScene(new Scene(root));
        dialog.setResizable(false);
        dialog.setOnCloseRequest(e -> btnCancel.fire());
    }

    public void bindToTask(javafx.concurrent.Task<?> task) {
        lblStatus.textProperty().bind(task.messageProperty());
        pb.progressProperty().bind(task.progressProperty());
        btnCancel.setOnAction(e -> task.cancel());
    }

    public void show() { dialog.show(); }
    public void close() { dialog.close(); }

    public void showDetails(String text) {
        taDetails.textProperty().unbind();
        taDetails.setText(text == null ? "" : text);
        if (!taDetails.isVisible()) {
            taDetails.setVisible(true);
            taDetails.setManaged(true);
        }
    }

    public void setIndeterminate() {
        pb.progressProperty().unbind();
        pb.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
    }

    public void setStatus(String text) {
        lblStatus.textProperty().unbind();
        lblStatus.setText(text);
    }
}