package client.utils.display;

import dto.ProgramFunctionDto;
import dto.RunResultDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProgramUtil {

    public static int getDisplayedProgramDegree(ProgramFunctionDto programInContextDetails) {
        int programDegree;
        programDegree = programInContextDetails.getDegree();
        return programDegree;
    }

    public static int getDisplayedProgramMaxDegree(ProgramFunctionDto programInContextDetails) {
        int programMaxDegree;
        programMaxDegree = programInContextDetails.getMaxDegree();
        return programMaxDegree;
    }

    public static List<String> getDisplayedProgramVariables(ProgramFunctionDto programInContextDetails) {
        List<String> displayedProgramVariables;
        displayedProgramVariables = programInContextDetails.getAllVariablesInOrder();
        return displayedProgramVariables;
    }

    public static List<String> getDisplayedProgramLabels(ProgramFunctionDto programInContextDetails) {
        List<String> displayedProgramLabels;
        displayedProgramLabels = programInContextDetails.getLabelsInOrder();
        return displayedProgramLabels;
    }

    public static List<Integer> generateNewExpandOptions(int currentDegree, int maxDegree) {
        List<Integer> newExpandOptions = new ArrayList<>();
        for(int i = currentDegree + 1; i <= maxDegree; i++) {
            newExpandOptions.add(i);
        }
        return newExpandOptions;
    }

    public static List<Integer> generateNewCollapseOptions(int currentDegree, int maxDegree) {
        List<Integer> newCollapseOptions = new ArrayList<>();
        for(int i = 0; i < currentDegree; i++) {
            newCollapseOptions.add(i);
        }
        return newCollapseOptions;
    }

    public static List<VariableRow> generateVariablesRowList(RunResultDto selectedRun){
        Map<String,Long> variables = selectedRun.getAllVariables();
        List<VariableRow> variableRows = new ArrayList<>();
        for(Map.Entry<String,Long> entry : variables.entrySet()){
            VariableRow variableRow = new VariableRow(entry.getKey(), entry.getValue());
            variableRows.add(variableRow);
        }
        return variableRows;
    }

    public static List<String> findChangedVariables(Map<String,Long> prevVariablesValues, Map<String,Long> currVariablesValues){
        List<String> changedVariables = new ArrayList<>();
        for(String variableName : prevVariablesValues.keySet()){
            if(currVariablesValues.get(variableName) != prevVariablesValues.get(variableName)){
                changedVariables.add(variableName);
            }
        }
        return changedVariables;
    }


}

