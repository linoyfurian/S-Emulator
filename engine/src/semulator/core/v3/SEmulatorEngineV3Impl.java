package semulator.core.v3;

import semulator.api.dto.FunctionDto;
import semulator.api.dto.ProgramDto;
import semulator.api.dto.ProgramFunctionDto;
import semulator.api.dto.RunResultDto;
import semulator.logic.Function.Function;
import semulator.logic.program.Program;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SEmulatorEngineV3Impl implements  SEmulatorEngineV3 {
    private Map<String, Program> programs = null;
    private Map<String, Program> functions = null;
    private Map<String, List<RunResultDto>> runsHistory = new HashMap<>(); //todo change string - username
    //private List<ExecutionRunDto> programRuns = new ArrayList<>();


    @Override
    public ProgramFunctionDto displayProgram(String name, boolean isProgram) {
        if (isProgram) {
            Program program = programs.get(name);
            return new ProgramDto(program);
        } else {
            Program function = functions.get(name);
            return new FunctionDto(function, functions);
        }
    }
}
