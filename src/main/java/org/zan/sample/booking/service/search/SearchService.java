package org.zan.sample.booking.service.search;

import org.zan.sample.booking.model.Booking;
import org.zan.sample.booking.model.Hotel;
import org.zan.sample.booking.model.RoomAvailability;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SearchService {
    void initHotels(List<Hotel> hotels);
    void initBookings(List<Booking> bookings);
    Optional<Hotel> findHotel(String hotelId);
    List<RoomAvailability> calculateAvailability(Hotel hotel, String roomType, LocalDate startDate, LocalDate endDate);
}
