package dto;

public class LoadReport {
    private final boolean success;
    private final String message;
    private final int programsNumber;
    private final int functionsNumber;

    public LoadReport(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.programsNumber = 0;
        this.functionsNumber = 0;

    }

    public LoadReport(boolean success, String message, int programsNumber, int functionsNumber) {
        this.success = success;
        this.message = message;
        this.programsNumber = programsNumber;
        this.functionsNumber = functionsNumber;

    }

    public int getProgramsNumber() {
        return programsNumber;
    }

    public int getFunctionsNumber() {
        return functionsNumber;
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
}
