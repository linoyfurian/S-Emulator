package semulator.api.dto;

import java.util.List;

public class ProgramDraft {
    private final String programName;
    private final List<InstructionDraft> instructions;

    public ProgramDraft(String programName,  List<InstructionDraft> instructions) {
        this.programName = programName;
        this.instructions = instructions;
    }
    public String getProgramName() {
        return programName;
    }
    public List<InstructionDraft> getInstructions() {
        return instructions;
    }
}