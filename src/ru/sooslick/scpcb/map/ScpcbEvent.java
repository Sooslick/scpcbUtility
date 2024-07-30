package ru.sooslick.scpcb.map;

public class ScpcbEvent {
    public final String event;
    public final ScpcbRoom room;

    public ScpcbEvent(String event, ScpcbRoom room) {
        this.event = event;
        this.room = room;
    }
}
