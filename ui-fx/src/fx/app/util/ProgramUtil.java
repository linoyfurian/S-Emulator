package fx.app.util;

import semulator.api.dto.FunctionDto;
import semulator.api.dto.InstructionDto;
import semulator.api.dto.ProgramDto;
import semulator.api.dto.ProgramFunctionDto;

import java.util.ArrayList;
import java.util.List;

public class ProgramUtil {

    public static int getDisplayedProgramDegree(String displayedProgramName, ProgramFunctionDto programDetails) {
        int programDegree = 0;

        if (displayedProgramName.equals(programDetails.getName()))
            programDegree = programDetails.getDegree();
        else {
            ProgramDto programDto = (ProgramDto) programDetails;
            FunctionDto displayedFunction = DisplayUtils.findFunctionToDisplay(displayedProgramName, programDto);
            if (displayedFunction != null)
                programDegree = displayedFunction.getDegree();
        }
        return programDegree;
    }

    public static int getDisplayedProgramMaxDegree(String displayedProgramName, ProgramFunctionDto programDetails) {
        int programMaxDegree = 0;

        if (displayedProgramName.equals(programDetails.getName()))
            programMaxDegree = programDetails.getMaxDegree();
        else {
            ProgramDto programDto = (ProgramDto) programDetails;
            FunctionDto displayedFunction = DisplayUtils.findFunctionToDisplay(displayedProgramName, programDto);
            if (displayedFunction != null)
                programMaxDegree = displayedFunction.getMaxDegree();
        }
        return programMaxDegree;
    }

    public static List<String> getDisplayedProgramVariables(String displayedProgramName, ProgramFunctionDto programDetails) {
        List<String> displayedProgramVariables =  new ArrayList<>();
        if (displayedProgramName.equals(programDetails.getName()))
            displayedProgramVariables = programDetails.getAllVariablesInOrder();
        else {
            ProgramDto programDto = (ProgramDto) programDetails;
            FunctionDto displayedFunction = DisplayUtils.findFunctionToDisplay(displayedProgramName, programDto);
            if (displayedFunction != null)
                displayedProgramVariables = displayedFunction.getAllVariablesInOrder();
        }
        return displayedProgramVariables;
    }

    public static List<String> getDisplayedProgramLabels(String displayedProgramName, ProgramFunctionDto programDetails) {
        List<String> displayedProgramLabels =  new ArrayList<>();
        if (displayedProgramName.equals(programDetails.getName()))
            displayedProgramLabels = programDetails.getLabelsInOrder();
        else {
            ProgramDto programDto = (ProgramDto) programDetails;
            FunctionDto displayedFunction = DisplayUtils.findFunctionToDisplay(displayedProgramName, programDto);
            if (displayedFunction != null)
                displayedProgramLabels = displayedFunction.getLabelsInOrder();
        }
        return displayedProgramLabels;
    }
}
