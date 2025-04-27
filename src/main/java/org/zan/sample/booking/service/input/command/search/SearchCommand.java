package org.zan.sample.booking.service.input.command.search;

public class SearchCommand {
    private final String hotelId;
    private final String roomType;
    private final int days;

    public SearchCommand(String hotelId, String roomType, int days) {
        this.hotelId = hotelId;
        this.roomType = roomType;
        this.days = days;
    }

    public String getHotelId() {
        return hotelId;
    }

    public String getRoomType() {
        return roomType;
    }

    public int getDays() {
        return days;
    }
}
