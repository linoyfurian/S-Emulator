package client.utils.display;

import dto.FunctionDto;
import dto.InstructionDto;
import dto.ProgramDto;
import dto.ProgramFunctionDto;

import java.util.List;

public class DisplayUtils {

    public static int getNumberOfBasicInstructions(ProgramFunctionDto program) {
        int numberOfBasicInstructions = 0;
        List<InstructionDto> instructions = program.getInstructions();
        for (InstructionDto instruction : instructions) {
            if(instruction.getType()=='B')
                numberOfBasicInstructions++;
        }
        return numberOfBasicInstructions;
    }

    public static FunctionDto findFunctionToDisplay(String programToDisplayName, ProgramDto programDetails){
        FunctionDto functionToDisplay = null;
        List<FunctionDto> functions = programDetails.getFunctions();
        for (FunctionDto function : functions) {
            if(function.getName().equals(programToDisplayName)){
                functionToDisplay = function;
                break;
            }
        }
        return functionToDisplay;
    }

    public static int getNumberOfArchitectureX(List<InstructionDto> instructions, String x) {
        int numberOfArchitectureX = 0;
        for (InstructionDto instruction : instructions) {
            if(instruction.getArchitecture().equals(x))
                numberOfArchitectureX++;
        }
        return numberOfArchitectureX;
    }

}
