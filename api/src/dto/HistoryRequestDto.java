package dto;

public class HistoryRequestDto {
    private final DebugContextDto debugContext;
    private final int degreeOfRun;
    private final boolean isProgram;
    private final String programInContext;
    private final String architecture;

    public HistoryRequestDto(DebugContextDto debugContext, int degreeOfRun, boolean isProgram, String programInContext, String architecture) {
        this.debugContext = debugContext;
        this.degreeOfRun = degreeOfRun;
        this.isProgram = isProgram;
        this.programInContext = programInContext;
        this.architecture = architecture;
    }

    public DebugContextDto getDebugContext() {
        return debugContext;
    }

    public int getDegreeOfRun() {
        return degreeOfRun;
    }

    public boolean isProgram() {
        return isProgram;
    }

    public String getProgramInContext() {
        return programInContext;
    }

    public String getArchitecture() {
        return architecture;
    }
}