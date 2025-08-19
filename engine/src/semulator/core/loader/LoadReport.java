package semulator.core.loader;

public class LoadReport {
    private final boolean success;
    private final String message;

    public LoadReport(boolean success, String message) {
        this.success = success;
        this.message = message;

    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
}
