package dto;

public class UserInfo {
    private final String name;
    private final int programsNumber;
    private final int functionsNumber;
    private final int credits;
    private final int usedCredits;
    private final int runsNumber;

    public UserInfo(String name){
        this.name = name;
        this.programsNumber = 0;
        this.functionsNumber = 0;
        this.credits = 0;
        this.usedCredits = 0;
        this.runsNumber = 0;
    }

    public String getName() {
        return name;
    }

    public int getProgramsNumber() {
        return programsNumber;
    }

    public int getFunctionsNumber() {
        return functionsNumber;
    }

    public int getCredits() {
        return credits;
    }

    public int getUsedCredits() {
        return usedCredits;
    }

    public int getRunsNumber() {
        return runsNumber;
    }
}
