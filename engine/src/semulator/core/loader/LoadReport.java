package semulator.core.loader;

import java.util.Collections;
import java.util.List;

public class LoadReport {
    private final boolean success;
    private final String message;
    private final List<String> errors;
    private final String programName;
    private final int inputsCount;
    private final int instructionsCount;

    public LoadReport(boolean success, String message, List<String> errors,
                      String programName, int inputsCount, int instructionsCount) {
        this.success = success;
        this.message = message;
        this.errors = (errors == null) ? List.of() : errors;
        this.programName = programName;
        this.inputsCount = inputsCount;
        this.instructionsCount = instructionsCount;
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public List<String> getErrors() { return Collections.unmodifiableList(errors); }
    public String getProgramName() { return programName; }
    public int getInputsCount() { return inputsCount; }
    public int getInstructionsCount() { return instructionsCount; }
}
