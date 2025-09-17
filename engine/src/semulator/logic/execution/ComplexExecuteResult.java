package semulator.logic.execution;

import semulator.logic.label.Label;

public class ComplexExecuteResult {
    private final Label nextLabel;
    private final int runCycles;

    public ComplexExecuteResult(Label nextLabel, int runCycles) {
        this.nextLabel = nextLabel;
        this.runCycles = runCycles;
    }

    public Label getNextLabel() {
        return nextLabel;
    }

    public int getRunCycles() {
        return runCycles;
    }
}
