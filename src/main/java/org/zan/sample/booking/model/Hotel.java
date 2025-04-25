package org.zan.sample.booking.model;

import java.util.List;

public class Hotel {
    private String id;
    private String name;

    private List<Room> rooms;

    public Hotel(String id, String name, List<Room> rooms) {
        this.id = id;
        this.name = name;
        this.rooms = rooms;
    }
}
