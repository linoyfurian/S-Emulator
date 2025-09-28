package semulator.api.dto;

public class InstructionDraft {
    private final String name;
    private final String mainVariable;
    private final String mainLabel;
    private final long constantValue;
    private final String additionalVariable;
    private final String additionalLabel;
    private final String command;
    private final int cycles;
    private final String type;

    public InstructionDraft(String name, String mainVariable, String mainLabel, long constantValue, String additionalVariable, String additionalLabel) {
        this.name = name;
        this.mainVariable = mainVariable;
        this.mainLabel = mainLabel;
        this.constantValue = constantValue;
        this.additionalVariable = additionalVariable;
        this.additionalLabel = additionalLabel;

        String commandDescription = "";
        String type = "";
        int cycles = 0;

        switch(this.name){
            case "ASSIGNMENT":
                commandDescription = (this.mainVariable + "<-" + this.additionalVariable);
                type = "S";
                cycles = 4;
                break;
            case "CONSTANT_ASSIGNMENT":
                commandDescription = (this.mainVariable + "<-" + this.constantValue);
                type = "S";
                cycles = 2;
                break;
            case "INCREASE":
                commandDescription = (this.mainVariable + "<-" + this.mainVariable + "+1");
                type = "B";
                cycles = 1;
                break;
            case "DECREASE":
                commandDescription = (this.mainVariable + "<-" + this.mainVariable + "-1");
                type = "B";
                cycles = 1;
                break;
            case "ZERO_VARIABLE":
                commandDescription = (this.mainVariable + "<-0");
                type = "S";
                cycles = 1;
                break;
            case "JUMP_NOT_ZERO":
                commandDescription = ("IF " + this.mainVariable + "!=0 GOTO " + this.additionalLabel);
                type = "B";
                cycles = 2;
                break;
            case "JUMP_ZERO":
                commandDescription = ("IF " + this.mainVariable + "=0 GOTO " + this.additionalLabel);
                type = "S";
                cycles = 2;
                break;
            case "NEUTRAL":
                commandDescription = (this.mainVariable + "<-" + this.mainVariable);
                type = "B";
                cycles = 0;
                break;
            case "GOTO_LABEL":
                commandDescription = ("GOTO " + this.additionalLabel);
                type = "S";
                cycles = 1;
                break;
            case "JUMP_EQUAL_CONSTANT":
                commandDescription = ("IF " + this.mainVariable + "=" + this.constantValue +  " GOTO " + this.additionalLabel);
                type = "S";
                cycles = 2;
                break;
            case "JUMP_EQUAL_VARIABLE":
                commandDescription = ("IF " + this.mainVariable + "=" + this.additionalVariable +  " GOTO " + this.additionalLabel);
                type = "S";
                cycles = 2;
                break;
        }

        this.command = commandDescription;
        this.type = type;
        this.cycles = cycles;
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
    public Long getConstantValue() {
        return constantValue;
    }
    public String getAdditionalVariable() {
        return additionalVariable;
    }
    public String getAdditionalLabel() {
        return additionalLabel;
    }

    public String getCommand() {
        return command;
    }

    public int getCycles() {
        return cycles;
    }

    public String getType() {
        return type;
    }

}
