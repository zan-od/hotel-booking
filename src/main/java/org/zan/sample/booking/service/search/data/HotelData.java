package org.zan.sample.booking.service.search.data;

import org.zan.sample.booking.model.Hotel;
import org.zan.sample.booking.model.Room;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HotelData {
    private final Hotel hotel;
    private final Map<String, Integer> roomsByRoomType;
    private final Map<String, List<BookingRange>> bookingsByRoomType;

    public HotelData(Hotel hotel, Map<String, List<BookingRange>> bookingsByRoomType) {
        this.hotel = hotel;
        this.roomsByRoomType = calculateRoomsByType(hotel);
        this.bookingsByRoomType = bookingsByRoomType;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public Map<String, Integer> getRoomsByRoomType() {
        return roomsByRoomType;
    }

    public Map<String, List<BookingRange>> getBookingsByRoomType() {
        return bookingsByRoomType;
    }

    private Map<String, Integer> calculateRoomsByType(Hotel hotel) {
        return hotel.getRooms().stream()
                .collect(Collectors.groupingBy(
                        Room::getType,
                        Collectors.summingInt(room -> 1)
                ));
    }
}
