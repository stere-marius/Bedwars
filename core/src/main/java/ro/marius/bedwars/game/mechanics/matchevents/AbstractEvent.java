package ro.marius.bedwars.game.mechanics.matchevents;

import ro.marius.bedwars.match.AMatch;

public abstract class AbstractEvent {

    private AMatch match;
    private String display;
    private String message;
    private int seconds;

    public AbstractEvent(AMatch match, String display,String message, int seconds) {
        this.match = match;
        this.display = display;
        this.seconds = seconds;
        this.message = message;
    }

    abstract void performEvent();

    protected String getDisplay() {
        return display;
    }

    protected int getSeconds() {
        return seconds;
    }

    public String getMessage() {
        return message;
    }
}
