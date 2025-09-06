package fx.app.util;

import semulator.api.dto.InstructionDto;
import semulator.api.dto.ProgramDto;

import java.util.List;

public class DisplayUtils {

    public static int getNumberOfBasicInstructions(ProgramDto program) {
        int numberOfBasicInstructions = 0;
        List<InstructionDto> instructions = program.getInstructions();
        for (InstructionDto instruction : instructions) {
            if(instruction.getType()=='B')
                numberOfBasicInstructions++;
        }
        return numberOfBasicInstructions;
    }
}
