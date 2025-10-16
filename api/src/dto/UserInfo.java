package dto;

public class UserInfo {
    private final String name;
    private  int programsNumber;
    private  int functionsNumber;
    private  long credits;
    private  long usedCredits;
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

    public long getCredits() {
        return credits;
    }

    public long getUsedCredits() {
        return usedCredits;
    }

    public int getRunsNumber() {
        return runsNumber;
    }

    public void setProgramsNumber(int programsNumber) {
        this.programsNumber = programsNumber;
    }

    public void setFunctionsNumber(int functionsNumber) {
        this.functionsNumber = functionsNumber;
    }

    public void setCredits(long credits) {
        this.credits = credits;
    }

    public void updateUsedCredits(long usedCredits) {
        this.usedCredits += usedCredits;
    }
    public void setUsedCredits(long usedCredits) {
        this.usedCredits = usedCredits;
    }

    public void setRunsNumber(int runsNumber) {
        this.runsNumber = runsNumber;
    }

    public void updateRunsNumber(){
        this.runsNumber++;
    }


}
