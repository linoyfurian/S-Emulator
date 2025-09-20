package semulator.api.dto;

public class InstructionDraft {
    private final String name;
    private final String mainVariable;
    private final String mainLabel;
    private final long constantValue;
    private final String additionalVariable;
    private final String additionalLabel;

    public InstructionDraft(String name, String mainVariable, String mainLabel, long constantValue, String additionalVariable, String additionalLabel) {
        this.name = name;
        this.mainVariable = mainVariable;
        this.mainLabel = mainLabel;
        this.constantValue = constantValue;
        this.additionalVariable = additionalVariable;
        this.additionalLabel = additionalLabel;
    }

    public String getName() {
        return name;
    }
    public String getMainVariable() {
        return mainVariable;
    }
    public String getMainLabel() {
        return mainLabel;
    }
    public long getConstantValue() {
        return constantValue;
    }
    public String getAdditionalVariable() {
        return additionalVariable;
    }
    public String getAdditionalLabel() {
        return additionalLabel;
    }

}
