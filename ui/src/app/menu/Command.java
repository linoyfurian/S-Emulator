package app.menu;

public enum Command {
    LOAD_XML(1, "Load program XML"),
    DISPLAY_PROGRAM(2, "Show program"),
    EXPAND(3, "Expand program"),
    RUN_PROGRAM(4, "Run program"),
    DISPLAY_HISTORY(5, "Show run history"),
    EXIT(6, "Exit");

    private final int code;
    private final String description;

    Command(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    // Map number -> Command
    public static Command fromCode(int code) {
        for (Command cmd : values()) {
            if (cmd.code == code) {
                return cmd;
            }
        }
        return null; // or throw IllegalArgumentException
    }

    @Override
    public String toString() {
        return code + ". " + description;
    }
}
