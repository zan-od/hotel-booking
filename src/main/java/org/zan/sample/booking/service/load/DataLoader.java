package org.zan.sample.booking.service.load;

import org.springframework.stereotype.Service;
import org.zan.sample.booking.model.Booking;
import org.zan.sample.booking.model.Hotel;
import org.zan.sample.booking.model.Room;
import org.zan.sample.booking.service.load.dto.BookingDto;
import org.zan.sample.booking.service.load.dto.HotelDto;
import org.zan.sample.booking.service.load.dto.RoomDto;
import org.zan.sample.booking.service.search.SearchService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.zan.sample.booking.service.input.AbstractCommandHandler.parseDate;

@Service
public class DataLoader {
    private final HotelLoader hotelLoader;
    private final BookingLoader bookingLoader;
    private final SearchService searchService;

    public DataLoader(HotelLoader hotelLoader, BookingLoader bookingLoader, SearchService searchService) {
        this.hotelLoader = hotelLoader;
        this.bookingLoader = bookingLoader;
        this.searchService = searchService;
    }

    public void loadData(String hotelDataFile, String bookingDataFile) {
        List<Hotel> hotels = hotelLoader.loadHotels(hotelDataFile).stream()
                .map(this::mapToHotel)
                .toList();

        Map<String, Hotel> hotelsById = hotels.stream().collect(Collectors.toMap(Hotel::getId, Function.identity()));

        List<Booking> bookings = bookingLoader.loadBookings(bookingDataFile).stream()
                .map(bookingDto -> mapToBooking(bookingDto, hotelsById))
                .toList();

        searchService.clearData();
        hotels.forEach(searchService::addHotel);
        bookings.forEach(searchService::addBooking);
    }


    private Hotel mapToHotel(HotelDto hotelDto) {
        return new Hotel(hotelDto.getId(), hotelDto.getName(),
                hotelDto.getRooms().stream()
                        .map(this::mapToRoom)
                        .toList());
    }

    private Room mapToRoom(RoomDto roomDto) {
        return new Room(roomDto.getRoomId(), roomDto.getRoomType());
    }

    private Booking mapToBooking(BookingDto bookingDto, Map<String, Hotel> hotelsById) {
        Hotel hotel = hotelsById.get(bookingDto.getHotelId());
        if (hotel == null) {
            throw new RuntimeException("Incorrect reference in booking - hotel not found: " + bookingDto.getHotelId());
        }

        LocalDate arrivalDate;
        LocalDate departureDate;
        try {
            arrivalDate = parseDate(bookingDto.getArrival());
            departureDate = parseDate(bookingDto.getDeparture());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Error parsing booking date", e);
        }

        return new Booking(
                hotel,
                bookingDto.getRoomType(),
                arrivalDate,
                departureDate
        );
    }
}
