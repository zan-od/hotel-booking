package org.zan.sample.booking.model;

import java.time.LocalDate;

public class RoomAvailability {
    private final Hotel hotel;
    private final String roomType;
    private final int roomCount;
    private final LocalDate startDate;
    private final LocalDate endDate;

    public RoomAvailability(Hotel hotel, String roomType, int roomCount, LocalDate startDate, LocalDate endDate) {
        this.hotel = hotel;
        this.roomType = roomType;
        this.roomCount = roomCount;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public String getRoomType() {
        return roomType;
    }

    public int getRoomCount() {
        return roomCount;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
}
