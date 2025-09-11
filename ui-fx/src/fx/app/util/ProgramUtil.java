package fx.app.util;

import semulator.api.dto.FunctionDto;
import semulator.api.dto.InstructionDto;
import semulator.api.dto.ProgramDto;
import semulator.api.dto.ProgramFunctionDto;

import java.util.ArrayList;
import java.util.List;

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


}
