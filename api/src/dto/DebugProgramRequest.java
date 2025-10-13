package dto;

import java.util.Map;

public class DebugProgramRequest {
    private final String programName;
    private final boolean isProgram;
    private final int degreeOfExpand;
    private final long[] inputs;
    private final Map<String, Long> originalInputs;
    private final DebugContextDto debugContext;
    private final int credits;

    public DebugProgramRequest(int credits, String programName, boolean isProgram, int degreeOfExpand, DebugContextDto context, Map<String, Long> originalInputs, long[] inputs) {
        this.programName = programName;
        this.isProgram = isProgram;
        this.degreeOfExpand = degreeOfExpand;
        this.originalInputs = originalInputs;
        this.inputs = inputs;
        this.debugContext = context;
        this.credits = credits;
    }

    public String getProgramName() {
        return programName;
    }
    public boolean isProgram() {
        return isProgram;
    }
    public int getDegreeOfExpand() {
        return degreeOfExpand;
    }
    public long[] getInputs() {
        return inputs;
    }
    public Map<String, Long> getOriginalInputs() {
        return originalInputs;
    }
    public DebugContextDto getDebugContext() {
        return debugContext;
    }

    public int getCredits() {
        return credits;
    }

}
