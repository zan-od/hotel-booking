package org.zan.sample.booking.model;

public class Room {
    private final String id;
    private final String type;

    public Room(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }
}
