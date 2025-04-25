package org.zan.sample.booking.service.search;

import org.zan.sample.booking.model.Booking;
import org.zan.sample.booking.model.Hotel;
import org.zan.sample.booking.model.RoomAvailability;

import java.time.LocalDate;
import java.util.List;

public interface SearchService {
    void initHotels(List<Hotel> hotels);
    void initBookings(List<Booking> bookings);
    RoomAvailability search(Hotel hotelName, String roomType, LocalDate startDate, LocalDate endDate);
}
