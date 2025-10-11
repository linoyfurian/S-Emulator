package dto;

import java.util.Map;

public class RunProgramRequest {
    private final String programName;
    private final boolean isProgram;
    private final int degreeOfExpand;
    private final long[] inputs;
    private final Map<String, Long> originalInputs;
    private final String architecture;

    public RunProgramRequest(String programName, String architecture, boolean isProgram, int degreeOfExpand,Map<String, Long> originalInputs, long[] inputs) {
        this.programName = programName;
        this.isProgram = isProgram;
        this.degreeOfExpand = degreeOfExpand;
        this.originalInputs = originalInputs;
        this.inputs = inputs;
        this.architecture = architecture;
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

    public String getArchitecture() {
        return architecture;
    }

}

