package semulator.api.dto;

import java.util.Map;

public class RunResultDto {
    private int runNumber;
    private final int degreeOfRun;
    private final long resultY;
    private final int cycles;
    private final Map<String,Long> inputs;
    private final Map<String,Long> allVariables;

    public RunResultDto(int runNumber, int degreeOfRun, long resultY, int cycles, Map<String,Long> inputs, Map<String,Long> allVariables) {
        this.runNumber = runNumber;
        this.degreeOfRun = degreeOfRun;
        this.resultY = resultY;
        this.cycles = cycles;
        this.inputs = inputs;
        this.allVariables = allVariables;
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
}
