package semulator.logic.Function;

import semulator.logic.instruction.expansion.ExpansionUtils;
import semulator.logic.program.Program;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

   public static boolean isFunctionExist(String functionName, List<Program> functions){
        for (Program function : functions) {
            if(function.getName().equals(functionName)){
                return true;
            }
        }
        return false;
    }

    public static Program findFunction(String functionName, List<Program> functions){
        for (Program function : functions) {
            if(function.getName().equals(functionName)){
                return function;
            }
        }
        return null;
    }

    public static String generateNewFunctionArguments(String functionArguments, Set<Integer> zUsedNumbers, Set<Integer> usedLabelsNumbers, Map<String, String> oldAndNew){
        String arguments = "";
        String currVariable = "";
        String newVariable = "";

        boolean isFunction = false;
        boolean isFunctionName = false;
        boolean isArg = true;

        for (int i = 0; i < functionArguments.length(); i++) {
            char c = functionArguments.charAt(i);
            if(c=='('){
                isFunction = true;
                isFunctionName = true;
                isArg = false;
                arguments = arguments + c;
            }
            else if(c==')'){
                isFunction = false;
                if(isArg){
                    if(!currVariable.equals("")){
                        if(oldAndNew.containsKey(currVariable)){
                            newVariable = oldAndNew.get(currVariable);
                        }
                        else{
                            int validZnumber = ExpansionUtils.findAvailableZNumber(zUsedNumbers);
                            zUsedNumbers.add(validZnumber);
                            newVariable = "z" + validZnumber;
                            oldAndNew.put(currVariable, newVariable);
                        }
                        arguments = arguments + newVariable + c;
                        currVariable = "";
                    }
                    else
                        arguments = arguments + c;
                }
                else
                    arguments = arguments + c;

                isFunctionName = false;
                isArg = true;
            }
            else if(c==',') {
                if (isFunction) {
                    if (isFunctionName) {
                        isFunctionName = false;
                        isArg = true;
                        arguments = arguments + c;
                    } else {
                        if (oldAndNew.containsKey(currVariable)) {
                            newVariable = oldAndNew.get(currVariable);
                        } else {
                            int validZnumber = ExpansionUtils.findAvailableZNumber(zUsedNumbers);
                            zUsedNumbers.add(validZnumber);
                            newVariable = "z" + validZnumber;
                            oldAndNew.put(currVariable, newVariable);
                        }
                        arguments = arguments + newVariable + c;
                        currVariable = "";
                    }
                }
                else
                    arguments = arguments + c;
            }
            else if(isArg){
                currVariable = currVariable + c;
            }
            else
                arguments = arguments + c;
        }

        return arguments;
    }

    public static String getFunctionName(String currArgument){
        String parts [] = currArgument.split(",");
        String functionName = "";
        if(parts.length == 1)
            functionName = parts[0].substring(1, parts[0].length()-1);
        else
            functionName = parts[0].substring(1);

        return functionName;
    }



    public static String getFunctionarguments(String currArgument) {
        String parts[] = currArgument.split(",");
        String functionArguments = "";
        if (parts.length > 1) {
            for (int i = 1; i < parts.length; i++) {
                if (i == parts.length - 1)
                    functionArguments = functionArguments + parts[i].substring(0, parts[i].length() - 1);
                else
                    functionArguments = functionArguments + parts[i] + ",";
            }
        }
        return functionArguments;
    }

    public static int findDepthOfFunction(String functionArguments){
        int depth = 1;

        for(int i=0; i<functionArguments.length(); i++){
            char c = functionArguments.charAt(i);
            if(c=='('){
                depth++;
            }
            else if(c==')'){
                if(i!=functionArguments.length()-1)
                    depth--;
            }
        }
        return depth;
    }
}
