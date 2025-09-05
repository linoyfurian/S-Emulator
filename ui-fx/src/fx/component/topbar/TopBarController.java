package fx.component.topbar;

import jakarta.xml.bind.JAXBException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import semulator.api.LoadReport;
import fx.system.SEmulatorSystemController;
import semulator.api.dto.ProgramDto;
import semulator.core.SEmulatorEngine;
import semulator.core.SEmulatorEngineImpl;

import java.nio.file.Path;
import java.nio.file.Paths;

public class TopBarController {
    private SEmulatorSystemController mainController;

    @FXML
    private TextField loadFileTextField;

    public void setMainController(SEmulatorSystemController mainController) {
        this.mainController = mainController;
    }


    public void btnLoadFileListener(ActionEvent event) {
        String fileName = loadFileTextField.getText().trim();
        System.out.println("Loading file: " + fileName);
        if (fileName.isEmpty()) {
            return;
        }

        if (mainController != null) {
            mainController.btnLoadFileListener(fileName);
            System.out.println("!!!!!!! ");
        }

        if(mainController==null) {
            System.out.println("NULLLLLL ");
        }
    }

    public void setLoadFileText(String newText) {
        loadFileTextField.setText(newText);
    }

//
//    public ProgramDto btnLoadFileListener(ActionEvent event) {
//        LoadReport loadReport = null;
//        Path path = null;
//
//        String fileName = loadFileTextField.getText();
//        path = Paths.get(fileName);
//        System.out.println(path);
//        try {
//            loadReport = engine.loadProgramDetails(path);
//        }
//        catch(JAXBException e){
//            loadFileTextField.setText("Error");
//        }
//        if(loadReport == null){
//            return null;
//        }
//        if(loadReport.isSuccess()) {
//            loadFileTextField.setText("Program Loaded successfully");
//            ProgramDto programDetails = engine.displayProgram();
//            System.out.println("programDetails");
//            return programDetails;
//        }
//        else{
//            loadFileTextField.setText(loadReport.getMessage());
//        }
//        return null;
//    }
}
