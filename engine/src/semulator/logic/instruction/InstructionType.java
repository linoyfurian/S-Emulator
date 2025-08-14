package semulator.logic.instruction;

public enum InstructionType {
    BASIC('B', "basic"),
    SYNTHETIC('S',"synthetic")
    ;

    private final char type;
    private final String xmlInstructionType;

    InstructionType(char type, String xmlInstructionType) {
        this.type = type;
        this.xmlInstructionType = xmlInstructionType;
    }

    public char getType() {
        return type;
    }

    public String getXmlInstructionType() {
        return xmlInstructionType;
    }
}