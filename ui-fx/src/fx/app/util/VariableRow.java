package fx.app.util;

public class VariableRow {
    private final String name;
    private final Long value;

    public VariableRow(String name, Long value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Long getValue() {
        return value;
    }
}
