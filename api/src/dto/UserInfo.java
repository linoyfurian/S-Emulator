package dto;

public class UserInfo {
    private final String name;
    private int programsNumber;
    private int functionsNumber;
    private long credits;
    private long usedCredits;
    private int runsNumber;

    public UserInfo(String name){
        this.name = name;
        this.programsNumber = 0;
        this.functionsNumber = 0;
        this.credits = 0;
        this.usedCredits = 0;
        this.runsNumber = 0;
    }

    public synchronized void updateProgramsNumber(int delta){
        this.programsNumber += delta;
    }

    public synchronized void updateFunctionsNumber(int delta){
        this.functionsNumber += delta;
    }

    public synchronized void setProgramsNumber(int programsNumber) {
        this.programsNumber = programsNumber;
    }

    public synchronized void setFunctionsNumber(int functionsNumber) {
        this.functionsNumber = functionsNumber;
    }

    public synchronized void setCredits(long credits) {
        this.credits = credits;
    }

    public synchronized void updateUsedCredits(long delta) {
        this.usedCredits += delta;
    }

    public synchronized void setUsedCredits(long usedCredits) {
        this.usedCredits = usedCredits;
    }

    public synchronized void setRunsNumber(int runsNumber) {
        this.runsNumber = runsNumber;
    }

    public synchronized void updateRunsNumber(){
        this.runsNumber++;
    }

    public String getName() { return name; }
    public int getProgramsNumber() { return programsNumber; }
    public int getFunctionsNumber() { return functionsNumber; }
    public long getCredits() { return credits; }
    public long getUsedCredits() { return usedCredits; }
    public int getRunsNumber() { return runsNumber; }
}
