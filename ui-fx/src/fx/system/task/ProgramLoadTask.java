import fx.app.util.ProgramUtil;
import jakarta.xml.bind.JAXBException;
import javafx.concurrent.Task;
import semulator.api.LoadReport;
import semulator.api.dto.ProgramDto;
import semulator.api.dto.ProgramFunctionDto;
import semulator.core.EngineState;
import semulator.core.SEmulatorEngine;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ProgramLoadTask extends Task<ProgramFunctionDto> {
    private final SEmulatorEngine engine;
    private final String xmlPath;

    ProgramLoadTask(String xmlPath, SEmulatorEngine engine) {
        this.xmlPath = xmlPath;
        this.engine = engine;
    }

    @Override
    protected ProgramFunctionDto call() throws Exception {
        ProgramFunctionDto programInContextDetails = null;

        updateMessage("Starting...");
        updateProgress(0, 100);

        // סימולציה קצרה
        sleep(200);

        // 1) Parse XML → ProgramDto
        updateMessage("Parsing XML...");


        LoadReport loadReport = null;
        Path path;
        path = Paths.get(xmlPath);

        try {
            loadReport = engine.loadProgramDetails(path);
        }
        catch(JAXBException e){

        }
        if(loadReport == null){
            return null;
        }
        if(loadReport.isSuccess()) {
            programInContextDetails = engine.displayProgram();
            updateMessage("Done");
            updateProgress(100, 100);
            sleep(150);
        }
        else{
            updateMessage("Failed: " + loadReport.getMessage());
            updateProgress(100, 100);
            sleep(150);
        }

        return programInContextDetails;
    }

    private void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}
