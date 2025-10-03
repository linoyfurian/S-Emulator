package dto;

public class FunctionInfo {
    private final String name;
    private final String programName;
    private final String userName;
    private final int instructionsNumber;
    private final int maxDegree;

    public FunctionInfo(String name, String programName, String userName, int instructionsNumber, int maxDegree) {
        this.name = name;
        this.programName = programName;
        this.userName = userName;
        this.instructionsNumber = instructionsNumber;
        this.maxDegree = maxDegree;
    }

    public String getName() {
        return name;
    }

    public String getProgramName() {
        return programName;
    }

    public String getUserName() {
        return userName;
    }

    public int getInstructionsNumber() {
        return instructionsNumber;
    }

    public int getMaxDegree() {
        return maxDegree;
    }

}
