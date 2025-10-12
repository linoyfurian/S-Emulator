package semulator.logic.execution;

import dto.ExecutionRunDto;
import semulator.core.loader.XmlProgramMapperV2;
import semulator.logic.Function.FunctionUtils;
import semulator.logic.program.Program;
import semulator.logic.program.ProgramImpl;

import java.util.List;
import java.util.Map;

public class ExecutionUtils {
    private ExecutionUtils() {}


    public static ArgumentResult findInputValue(Map<String, Program> functions, String argument, ExecutionContext context) {
        long result = 0L;
        int cycles = 0;

        // Case 1: simple variable
        if(!FunctionUtils.isVariableIsAFunction(argument)) {
            result = context.getVariableValue(XmlProgramMapperV2.variableMapper(argument));
        }
        // Case 2: argument is a function call
        else {
            String funcName = FunctionUtils.getFunctionName(argument);
            String funcArgs = FunctionUtils.getFunctionarguments(argument);

            // Find the function in the program
            Program targetFunction = functions.get(funcName);

            if(targetFunction != null) {
                // Split and evaluate each argument
                List<String> innerArgs = FunctionUtils.splitFunctionArguments(funcArgs);
                long[] inputs = new long[innerArgs.size()];
                for(int i = 0; i < innerArgs.size(); i++) {
                    ArgumentResult currArgumentResult = findInputValue(functions, innerArgs.get(i), context);
                    inputs[i] = currArgumentResult.getArgumentValue();
                    cycles += currArgumentResult.getCycles();
                }

                // Run the function
                ProgramExecutor executor = new ProgramExecutorImpl(targetFunction, functions);

                ExecutionRunDto runDetails = executor.run(-1, 0, 0, null, inputs);
                if(runDetails != null) {
                    result = runDetails.getResult();
                    cycles = cycles + runDetails.getCycles();
                }
            }
        }

        return new ArgumentResult(result, cycles);
    }
}
