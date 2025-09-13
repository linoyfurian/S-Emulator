package semulator.logic.Function;

import semulator.logic.instruction.expansion.ExpansionUtils;
import semulator.logic.program.Program;

import java.util.ArrayList;
import java.util.List;

public class FunctionUtils {
    private FunctionUtils() {}



    public static String mapArgument(String functionArg, List<Program> functions) {
        String result = "";
        boolean isFunctionName = true;
        boolean isArg = false;
        boolean isFunction = false;
        String functionName = "";

        for (int i = 0; i < functionArg.length(); i++) {
            char c = functionArg.charAt(i);
            if(c=='('){
                isFunctionName = true;
                isFunction = true;
                isArg = false;
                result += c;
            }
            else if(c==','){
                if(isFunction) {
                    if (isFunctionName) {
                        isFunctionName = false;
                        isArg = true;
                        isFunctionName = false;
                        isArg = true;
                        Function currFunction = ExpansionUtils.findFunctionInProgram(functions, functionName);
                        String userStringFunctionName = currFunction.getUserString();
                        result += userStringFunctionName;
                        functionName = "";
                    }
                }
                result += c;
            }
            else if(c==')'){
                if(isFunctionName) {
                    Function currFunction = ExpansionUtils.findFunctionInProgram(functions, functionName);
                    String userStringFunctionName = currFunction.getUserString();
                    result += userStringFunctionName;
                }
                isFunction = false;
                isFunctionName = false;
                isArg = true;
                functionName = "";
                result += c;
            }
            else{
                if(isArg)
                    result += c;
                else
                    functionName += c;
            }
        }
        return result;
    }


    public static List<String> splitFunctionArguments(String functionArguments) {
        List<String> arguments = new ArrayList<>();

        boolean isFunction = false;
        String currFunctionArgument="";

        for (int i = 0; i < functionArguments.length(); i++) {
            char c = functionArguments.charAt(i);
            if(c=='('){
                isFunction = true;
                currFunctionArgument = currFunctionArgument + c;
            }
            else if(c==','){
                if(isFunction)
                    currFunctionArgument = currFunctionArgument + c;
                else{
                    arguments.add(currFunctionArgument);
                    currFunctionArgument ="";
                }
            }
            else if(c==')'){
                currFunctionArgument = currFunctionArgument + c;
                isFunction = false;
            }
            else
                currFunctionArgument = currFunctionArgument + c;
        }

        if (!currFunctionArgument.equals(""))
            arguments.add(currFunctionArgument);

        return arguments;

    }


    public static String generateUserStringFunctionArguments(String functionArguments, List<Program> functions) {
        String result;
        boolean isFunction;

        String userStringFunctionArgument;

        List<String> functionArgs = splitFunctionArguments(functionArguments);
        List<String> newFunctionArguments = new ArrayList<>();

        for (String functionArg : functionArgs) {
            isFunction = isVariableIsAFunction(functionArg);
            if (isFunction) {
                userStringFunctionArgument = FunctionUtils.mapArgument(functionArg, functions);
                newFunctionArguments.add(userStringFunctionArgument);
            } else
                newFunctionArguments.add(functionArg);
        }

        result = String.join(",", newFunctionArguments);

        return result;
    }

    public static boolean isVariableIsAFunction(String variableName) {
        boolean result = false;
        variableName = variableName.trim();
        if(variableName.startsWith("("))
            result = true;

        return result;
    }
}
