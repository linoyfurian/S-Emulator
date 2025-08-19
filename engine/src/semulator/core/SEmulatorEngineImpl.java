package semulator.core;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import semulator.core.loader.LoadReport;
import semulator.core.loader.XmlProgramMapper;
import semulator.core.loader.jaxb.schema.generated.SProgram;
import semulator.logic.execution.ExecutionContext;
import semulator.logic.execution.ProgramExecutor;
import semulator.logic.execution.ProgramExecutorImpl;
import semulator.logic.program.Program;
import semulator.logic.program.ProgramDto;

import java.nio.file.Files;
import java.util.Locale;
import java.nio.file.Path;

public class SEmulatorEngineImpl implements SEmulatorEngine {
    private Program program = null;
    private boolean isLoaded = false;

    @Override
    public ProgramDto displayProgram(){
        return new ProgramDto(program);
    }

    @Override
    public LoadReport loadProgramDetails(Path filePath) throws JAXBException {
        LoadReport loadReport = null;
        if (filePath == null) {
            loadReport = new LoadReport(false, "Error: No path was provided");
            return loadReport;
        }
        if (!hasXmlExtension(filePath)) { // not xml
            loadReport = new LoadReport(false, "Error: No XML  file. File must have .xml extension");
            return loadReport;
        }

        if (!Files.exists(filePath)) {
            loadReport = new LoadReport(false, "Error: File " + filePath.getFileName() + " does not exist");
            return  loadReport;
        }

        try {
            JAXBContext ctx = JAXBContext.newInstance("semulator.core.loader.jaxb.schema.generated");
            Unmarshaller u = ctx.createUnmarshaller();
            SProgram sProgram = (SProgram) u.unmarshal(filePath.toFile());
            Program mappedProgram;
            mappedProgram = XmlProgramMapper.fromSProgramToProgramImpl(sProgram);
            boolean isValidProgram = mappedProgram.validate();
            if (isValidProgram) {
                this.program = mappedProgram;
                this.isLoaded = true;
                loadReport = new LoadReport(true, "Program loaded successfully");
                return loadReport;
            }
            else {
                loadReport = new LoadReport(false, "Error: Program is not valid, there is a reference to a label that doesnt exits");
                return loadReport;
            }
        }
        catch (JAXBException e) {
            loadReport = new LoadReport(false, e.getMessage());
            return loadReport;
        }
    }

    private static boolean hasXmlExtension(Path path){
        if (path == null) return false;
        String name = path.getFileName().toString().trim();
        return name.toLowerCase(Locale.ROOT).endsWith(".xml");
    }

    @Override
    public ExecutionContext runProgram(int desiredDegreeOfExpand, Long ... input){
        Program programToRun = program.expand(desiredDegreeOfExpand);
        ProgramExecutor programExecutor = new ProgramExecutorImpl(programToRun);

        ExecutionContext runResult = programExecutor.run(input);

        return runResult;
    }

    @Override
    public ProgramDto expand(int desiredDegreeOfExpand){
        Program expandedProgram = program.expand(desiredDegreeOfExpand);
        ProgramDto programDto = new ProgramDto(expandedProgram);
        return programDto;
    }
}