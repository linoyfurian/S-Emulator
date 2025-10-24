package semulator.core.loader;

public class Validator {
    private final boolean valid;
    private final String message;

    public Validator(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }

    public boolean isValid() {
        return valid;
    }
    public String getMessage() {
        return message;
    }

}
