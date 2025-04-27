package org.zan.sample.booking.service.input.command.availability;

import java.time.LocalDate;

public class AvailabilityCommand {
    private final String hotelId;
    private final String roomType;
    private final LocalDate startDate;
    private final LocalDate endDate;

    public AvailabilityCommand(String hotelId, String roomType, LocalDate startDate, LocalDate endDate) {
        this.hotelId = hotelId;
        this.roomType = roomType;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getHotelId() {
        return hotelId;
    }

    public String getRoomType() {
        return roomType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }
}
