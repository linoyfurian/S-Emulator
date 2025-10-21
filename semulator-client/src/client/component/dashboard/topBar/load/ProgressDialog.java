package client.component.dashboard.topBar.load;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ProgressDialog {
    private final Stage dialog;
    private final Label lblStatus;
    private final ProgressBar pb;
    private final TextArea taDetails;
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
        btnCancel.setOnAction(e -> close());

        VBox root = new VBox(10, lblStatus, pb, taDetails, btnCancel);
        root.setPadding(new Insets(12));
        root.setFillWidth(true);
        root.setPrefWidth(420);
        root.setPrefHeight(150);

        lblStatus.setMaxWidth(Double.MAX_VALUE);
        lblStatus.prefWidthProperty().bind(root.widthProperty().subtract(24));

        dialog.setScene(new Scene(root));
        dialog.setResizable(false);
        dialog.setOnCloseRequest(e -> close());
    }

    public void show() { dialog.show(); }
    public void close() { dialog.close(); }

    public void startLoading(String message) {
        lblStatus.textProperty().unbind();
        lblStatus.setText(message == null ? "Loading…" : message);

        pb.progressProperty().unbind();
        pb.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        pb.setVisible(true);
        pb.setManaged(true);

        btnCancel.setText("Cancel");
        btnCancel.setDisable(false);
        btnCancel.setVisible(true);
        btnCancel.setManaged(true);
    }

    private void enterResultMode(String message) {
        lblStatus.setStyle("-fx-text-fill: #d60202; -fx-font-weight: bold;");
        lblStatus.setText(message == null ? "" : message);

        pb.progressProperty().unbind();
        pb.setVisible(false);
        pb.setManaged(false);

        taDetails.setVisible(false);
        taDetails.setManaged(false);

        btnCancel.setText("Close");
        btnCancel.setDisable(false);
        btnCancel.setVisible(true);
        btnCancel.setManaged(true);
    }

    public void finishSuccess(String successMessage) {
        enterResultMode(successMessage == null ? "Loaded successfully." : successMessage);
    }

    public void finishError(String errorMessage) {
        enterResultMode((errorMessage == null ? "Unknown error" : errorMessage));
    }
}
