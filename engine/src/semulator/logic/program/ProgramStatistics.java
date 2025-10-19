package semulator.logic.program;

public class ProgramStatistics {
    private int runsNumber;
    private double creditsAverage;

    public ProgramStatistics(int runsNumber, double creditsAverage) {
        this.runsNumber = runsNumber;
        this.creditsAverage = creditsAverage;
    }

    public int getRunsNumber() {
        return runsNumber;
    }
    public double getCreditsAverage() {
        return creditsAverage;
    }

    public synchronized void updateCreditsAverage(int credits) {
        this.creditsAverage = ((this.runsNumber * this.creditsAverage) + credits)/(this.runsNumber+1);
        this.runsNumber++;
    }
}
