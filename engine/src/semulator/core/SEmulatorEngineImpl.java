package semulator.core;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import semulator.core.loader.XmlProgramMapper;
import semulator.core.loader.jaxb.schema.generated.SProgram;
import semulator.logic.execution.ExecutionContext;
import semulator.logic.execution.ProgramExecutor;
import semulator.logic.execution.ProgramExecutorImpl;
import semulator.logic.instruction.Instruction;
import semulator.logic.instruction.JumpInstruction;
import semulator.logic.program.Program;
import semulator.logic.program.ProgramDto;

import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.nio.file.Path;
import java.util.Set;


public class SEmulatorEngineImpl implements SEmulatorEngine {
    private Program program = null;
    private boolean isLoaded = false;


    @Override
    public ProgramDto displayProgram(){
        return new ProgramDto(program);
    }

    @Override
    public void loadProgramDetails(Path filePath) throws JAXBException {
        if (filePath == null) {
            // TODO: record/report error to UI: "No path was provided."
            return;
        }
        if (!hasXmlExtension(filePath)) { // not xml
            // TODO: record/report error: "File must have .xml extension"
            return;
        }

        if (!Files.exists(filePath)) {
            // TODO: file not found
            return;
        }

        try {
            JAXBContext ctx = JAXBContext.newInstance("semulator.core.loader.jaxb.schema.generated");
            Unmarshaller u = ctx.createUnmarshaller();
            SProgram sProgram = (SProgram) u.unmarshal(filePath.toFile());
            Program mappedProgram;
            mappedProgram = XmlProgramMapper.fromSProgramToProgramImpl(sProgram);
            boolean isValidProgram = checkProgramValidation(mappedProgram);
            if (isValidProgram) {
                this.program = mappedProgram;
                this.isLoaded = true;
            }
            else {
                // TODO: there is a reference to a label that doesnt exits
            }
        }
        catch (JAXBException e) {
            // TODO: handle exception
        }
    }

    private static boolean hasXmlExtension(Path path){
        if (path == null) return false;
        String name = path.getFileName().toString().trim();
        return name.toLowerCase(Locale.ROOT).endsWith(".xml");
    }

    private boolean checkProgramValidation(Program mappedProgram){
        boolean valid = true;

        if (mappedProgram == null) {
            return false;
        }

        Set<String> definedLabels = new HashSet<>();
        List<Instruction> instructions = mappedProgram.getInstructions();

        for (Instruction instr : instructions) {
            String rep = instr.getLabel().getLabelRepresentation();
            if (!rep.isEmpty()) {
                definedLabels.add(rep);
            }
        }

        for (Instruction instr : instructions) {
            if (instr instanceof JumpInstruction ji) {
                String target = ji.getTargetLabel().getLabelRepresentation();
                if (target.isEmpty()) {
                    valid = false;
                    break;
                }
                if (!definedLabels.contains(target)) {
                    valid = false;
                    break;
                }
            }
        }

        return valid;
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