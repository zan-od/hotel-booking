package org.zan.sample.booking.service.search;

import org.springframework.stereotype.Service;
import org.zan.sample.booking.model.Booking;
import org.zan.sample.booking.model.Hotel;
import org.zan.sample.booking.model.RoomAvailability;

import java.time.LocalDate;
import java.util.*;

@Service
public class InMemorySearchService implements SearchService {
    private final Map<String, Hotel> hotels = new HashMap<>();
    private final List<Booking> bookings = new ArrayList<>();

    @Override
    public void clearData() {
        hotels.clear();
        bookings.clear();
    }

    @Override
    public void addHotel(Hotel hotel) {
        hotels.put(hotel.getId(), hotel);
    }

    @Override
    public void addBooking(Booking booking) {
        bookings.add(booking);
    }

    @Override
    public Optional<Hotel> findHotel(String hotelId) {
        return Optional.ofNullable(hotels.get(hotelId));
    }

    @Override
    public List<RoomAvailability> calculateAvailability(Hotel hotel, String roomType, LocalDate startDate, LocalDate endDate) {
        // Implement logic to calculate room availability
        return List.of();
    }
}
