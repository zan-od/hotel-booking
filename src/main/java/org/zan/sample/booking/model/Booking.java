package org.zan.sample.booking.model;

import java.time.LocalDate;

public class Booking {
    private Hotel hotel;
    private String roomType;
    private LocalDate arrivalDate;
    private LocalDate departureDate;

    public Booking(Hotel hotel, String roomType, LocalDate arrivalDate, LocalDate departureDate) {
        this.hotel = hotel;
        this.roomType = roomType;
        this.arrivalDate = arrivalDate;
        this.departureDate = departureDate;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public LocalDate getArrivalDate() {
        return arrivalDate;
    }

    public String getRoomType() {
        return roomType;
    }
}
