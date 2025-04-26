package org.zan.sample.booking.service.search.data;

import org.zan.sample.booking.model.Hotel;
import org.zan.sample.booking.model.Room;

import java.util.Map;
import java.util.stream.Collectors;

public class HotelData {
    private Hotel hotel;
    private Map<String, Integer> roomsByType;

    public HotelData(Hotel hotel) {
        this.hotel = hotel;
        this.roomsByType = calculateRoomsByType(hotel);
    }

    public Hotel getHotel() {
        return hotel;
    }

    public Map<String, Integer> getRoomsByType() {
        return roomsByType;
    }

    private Map<String, Integer> calculateRoomsByType(Hotel hotel) {
        return hotel.getRooms().stream()
                .collect(Collectors.groupingBy(
                        Room::getType,
                        Collectors.summingInt(room -> 1)
                ));
    }
}
