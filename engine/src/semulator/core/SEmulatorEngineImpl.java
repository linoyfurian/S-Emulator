package semulator.core;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import semulator.core.loader.XmlProgramMapper;
import semulator.core.loader.jaxb.schema.generated.SProgram;
import semulator.logic.instruction.Instruction;
import semulator.logic.instruction.JumpInstruction;
import semulator.logic.program.Program;
import semulator.logic.program.ProgramData;
import semulator.logic.program.ProgramImpl;

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
    public ProgramData displayProgram(){
        return new ProgramData(program);
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
            // file not found
            return;
        }

        //xml file

        try {
            JAXBContext ctx = JAXBContext.newInstance("semulator.core.loader.jaxb.schema.generated");
            Unmarshaller u = ctx.createUnmarshaller();
            SProgram sProgram = (SProgram) u.unmarshal(filePath.toFile());
            ProgramImpl mappedProgram;
            mappedProgram = XmlProgramMapper.fromSProgramToProgramImpl(sProgram);
            boolean isValidProgram = checkProgramValidation(mappedProgram);
            if (isValidProgram) {
                this.program = mappedProgram;
                this.isLoaded = true;
            }
            else {
                //there is a reference to a label that doesnt exits
            }
        }
        catch (JAXBException e) {}
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
    public void runProgram(){

    }
}

