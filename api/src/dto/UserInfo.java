package dto;

public class UserInfo {
    private final String name;
    private  int programsNumber;
    private  int functionsNumber;
    private  int credits;
    private  int usedCredits;
    private  int runsNumber;

    public UserInfo(String name){
        this.name = name;
        this.programsNumber = 0;
        this.functionsNumber = 0;
        this.credits = 0;
        this.usedCredits = 0;
        this.runsNumber = 0;
    }

    public void updateProgramsNumber(int programsNumber){
        this.programsNumber+=programsNumber;
    }

    public void updateFunctionsNumber(int functionsNumber){
        this.functionsNumber+=functionsNumber;
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
