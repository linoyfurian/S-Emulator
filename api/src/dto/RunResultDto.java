package dto;

import java.util.Map;

public class RunResultDto {
    private int runNumber;
    private final int degreeOfRun;
    private final long resultY;
    private final int cycles;
    private final Map<String,Long> inputs;
    private final Map<String,Long> allVariables;
    private final String programOrFunction;
    private final String architecture;
    private final String programName;
    private final String originalName;

    public RunResultDto(int runNumber, int degreeOfRun, long resultY, int cycles, Map<String,Long> inputs, Map<String,Long> allVariables, String programOrFunction, String architecture, String programName,  String originalName) {
        this.runNumber = runNumber;
        this.degreeOfRun = degreeOfRun;
        this.resultY = resultY;
        this.cycles = cycles;
        this.inputs = inputs;
        this.allVariables = allVariables;
        this.programOrFunction = programOrFunction;
        this.architecture = architecture;
        this.programName = programName;
        this.originalName = originalName;
    }

    public int getRunNumber() {
        return runNumber;
    }

    public int getDegreeOfRun() {
        return degreeOfRun;
    }

    public long getResultY() {
        return resultY;
    }

    public int getCycles() {
        return cycles;
    }

    public Map<String,Long> getAllVariables() {
        return allVariables;
    }

    public Map<String,Long> getInputs() {
        return inputs;
    }

    public String getProgramOrFunction() {
        return programOrFunction;
    }

    public String getArchitecture() {
        return architecture;
    }

    public String getName() {
        return programName;
    }

    public String getOriginalName() {
        return originalName;
    }
}
