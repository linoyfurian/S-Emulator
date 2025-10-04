package semulator.core.v3;

import dto.LoadReport;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import semulator.api.dto.FunctionDto;
import semulator.api.dto.ProgramDto;
import semulator.api.dto.ProgramFunctionDto;
import semulator.api.dto.RunResultDto;
import semulator.core.loader.XmlProgramMapperV2;
import semulator.core.loader.jaxb.schema.version2.generated.SProgram;
import semulator.logic.Function.Function;
import semulator.logic.program.Program;
import semulator.logic.program.ProgramImpl;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SEmulatorEngineV3Impl implements  SEmulatorEngineV3 {
    private Map<String, Program> programs = new HashMap<>();
    private Map<String, Program> functions = new HashMap<>();
    private Map<String, List<RunResultDto>> runsHistory = new HashMap<>(); //todo change string - username
    //private List<ExecutionRunDto> programRuns = new ArrayList<>();

    private static final JAXBContext JAXB_CTX;
    static {
        try {
            JAXB_CTX = JAXBContext.newInstance("semulator.core.loader.jaxb.schema.version2.generated");
        } catch (JAXBException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @Override
    public ProgramFunctionDto displayProgram(String name, boolean isProgram) {
        if (isProgram) {
            Program program = programs.get(name);
            return new ProgramDto(program, functions);
        } else {
            Program function = functions.get(name);
            return new FunctionDto(function, functions);
        }
    }

    @Override
    public LoadReport loadProgramDetails(InputStream in) throws JAXBException {
        if (in == null) {
            return new LoadReport(false, "Error: No input stream was provided");
        }

        try {
            Unmarshaller u = JAXB_CTX.createUnmarshaller();
            SProgram sProgram = (SProgram) u.unmarshal(in);

            Program mappedProgram = XmlProgramMapperV2.fromSProgramToProgramImpl(sProgram);

            boolean isValidProgram = mappedProgram.validate();
            if (!isValidProgram) {
                return new LoadReport(false, "Error: Program is not valid, there is a reference to a label that doesnt exits");
            }

            ProgramImpl programImpl = (ProgramImpl) mappedProgram;

            if (programImpl.hasInvalidFunctionReferences(functions)) {
                return new LoadReport(false, "Error: Program is not valid, there is a reference in the program to a function that doesnt exits");
            }

            List<Program> funcs = programImpl.getFunctions();
            for (Program f : funcs) {
                Function fn = (Function) f;
                if (fn.hasInvalidFunctionReferences(funcs, functions)) {
                    return new LoadReport(false, "Error: Program is not valid, there is a reference in a function to a function that doesnt exits");
                }
            }

            this.programs.put(mappedProgram.getName(), mappedProgram);

            for (Program f : funcs) {
                Function fn = (Function) f;
                this.functions.put(fn.getName(), fn);
            }

            this.runsHistory.clear();

            return new LoadReport(true, "Program loaded successfully");

        } catch (JAXBException e) {
            return new LoadReport(false, e.getMessage());
        }
    }
}
