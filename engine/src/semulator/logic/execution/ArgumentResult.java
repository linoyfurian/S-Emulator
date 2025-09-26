package semulator.logic.execution;

public class ArgumentResult {
    private final long argumentValue;
    private final int cycles;

    public ArgumentResult(long argumentValue, int cycles) {
        this.argumentValue = argumentValue;
        this.cycles = cycles;
    }

    public long getArgumentValue() {
        return argumentValue;
    }

    public int getCycles() {
        return cycles;
    }
}
