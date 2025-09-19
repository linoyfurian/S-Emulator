package fx.app.display;

public enum Theme {
    Default( "Default"),
    Dark("Dark"),
    Pink("Pink");

    public final String display;

    Theme(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }
}
