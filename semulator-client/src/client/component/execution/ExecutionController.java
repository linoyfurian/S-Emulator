package client.component.execution;

import client.component.dashboard.DashboardController;
import client.component.execution.debugger.DebuggerController;
import client.component.execution.instructions.InstructionsController;
import client.component.execution.topbar.TopbarExecutionController;
import client.utils.display.ProgramUtil;
import dto.ProgramFunctionDto;
import dto.RunResultDto;
import javafx.fxml.FXML;

import java.util.List;

public class ExecutionController {
    DashboardController mainController;

    @FXML private TopbarExecutionController topBarExecutionController;
    @FXML private InstructionsController instructionsController;
    @FXML private DebuggerController debuggerController;

    @FXML
    public void initialize() {
        if (topBarExecutionController != null) {
            topBarExecutionController.setMainController(this);
        }
        if (instructionsController != null) {
            instructionsController.setMainController(this);
        }
        if (debuggerController != null) {
            debuggerController.setMainController(this);
        }
    }

    public void setMainController(DashboardController mainController) {
        this.mainController = mainController;
    }

    public void setUserName(String userName) {
        this.topBarExecutionController.setUserName(userName);
    }

    public void initialProgramDetails(ProgramFunctionDto programDetails){
        instructionsController.displayProgram(programDetails);

        int programDegree = ProgramUtil.getDisplayedProgramDegree(programDetails);
        int maxDegree = ProgramUtil.getDisplayedProgramMaxDegree(programDetails);
        topBarExecutionController.updateDegreeLabel(programDegree, maxDegree);

        topBarExecutionController.refreshHighlightOptions(programDetails);
        debuggerController.setProgram(programDetails);
        instructionsController.resetBreakPointSelection();
    }

    public void onHighlightChangedListener(String highlightSelected){
        instructionsController.highlightSelectionOnTable(highlightSelected);
    }
}
