package dto;

import java.util.List;

public interface ProgramFunctionDto {
    String getName();
    List<String> getInputVariablesInOrder();
    List<String> getLabelsInOrder();
    List<InstructionDto> getInstructions();
    int getDegree();
    List<String> getAllVariablesInOrder();
    int getMaxDegree();
}