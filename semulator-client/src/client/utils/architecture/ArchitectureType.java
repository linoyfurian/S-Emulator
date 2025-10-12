package client.utils.architecture;

public enum ArchitectureType {
    I("I", 5),
    II("II",100),
    III("III", 500),
    IV("IV",1000);

    private final String type;
    private final int runCost;

    ArchitectureType(String type, int runCost) {
        this.type = type;
        this.runCost = runCost;
    }

    public String getType() {
        return type;
    }

    public int getRunCost() {
        return runCost;
    }

}
