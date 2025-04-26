package org.zan.sample.booking.model;

import java.util.List;

public class Hotel {
    private final String id;
    private final String name;
    private final List<Room> rooms;

    public Hotel(String id, String name, List<Room> rooms) {
        this.id = id;
        this.name = name;
        this.rooms = rooms;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Room> getRooms() {
        return rooms;
    }
}
