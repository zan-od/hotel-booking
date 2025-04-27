package org.zan.sample.booking.service.search;

import org.zan.sample.booking.model.Booking;
import org.zan.sample.booking.model.Hotel;
import org.zan.sample.booking.model.RoomAvailability;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SearchService {
    void clearData();
    void loadData(List<Hotel> hotels, List<Booking> bookings);
    List<RoomAvailability> calculateAvailability(String hotelId, String roomType, LocalDate startDate, LocalDate endDate);
}
