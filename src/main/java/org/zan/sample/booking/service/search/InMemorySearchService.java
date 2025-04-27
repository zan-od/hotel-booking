package org.zan.sample.booking.service.search;

import org.springframework.stereotype.Service;
import org.zan.sample.booking.model.Booking;
import org.zan.sample.booking.model.Hotel;
import org.zan.sample.booking.model.RoomAvailability;
import org.zan.sample.booking.service.search.data.BookingRange;
import org.zan.sample.booking.service.search.data.HotelData;
import org.zan.sample.booking.service.search.exception.EntityNotFoundException;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.*;
import static java.util.stream.Collectors.groupingBy;

@Service
public class InMemorySearchService implements SearchService {
    private final Map<String, HotelData> hotelsById = new HashMap<>();

    private final RangeCalculator rangeCalculator;

    public InMemorySearchService(RangeCalculator rangeCalculator) {
        this.rangeCalculator = rangeCalculator;
    }

    @Override
    public void clearData() {
        hotelsById.clear();
    }

    @Override
    public void loadData(List<Hotel> hotels, List<Booking> bookings) {
        Map<Hotel, List<Booking>> bookingsByHotel = bookings.stream()
                .collect(groupingBy(Booking::getHotel, toList()));

        for (Hotel hotel : hotels) {
            Map<String, List<BookingRange>> bookingsByRoomType;

            List<Booking> hotelBookings = bookingsByHotel.get(hotel);
            if (hotelBookings != null) {
                bookingsByRoomType = hotelBookings.stream()
                        .collect(
                                groupingBy(Booking::getRoomType,
                                        collectingAndThen(toList(), this::convertAndMergeRanges))
                        );
            } else {
                bookingsByRoomType = new HashMap<>();
            }

            HotelData hotelData = new HotelData(hotel, bookingsByRoomType);
            hotelsById.put(hotel.getId(), hotelData);
        }
    }

    private List<BookingRange> convertAndMergeRanges(List<Booking> bookings) {
        return mergeBookingRanges(groupBookingRanges(bookings));
    }

    private List<BookingRange> groupBookingRanges(List<Booking> bookings) {
        Map<BookingRange, Integer> groupedRanges = bookings.stream()
                .map(b -> new BookingRange(b.getArrivalDate(), b.getDepartureDate(), 1))
                .collect(groupingBy(Function.identity(), summingInt(BookingRange::getCount)));
        return groupedRanges.entrySet().stream()
                .map(e -> new BookingRange(e.getKey().getStartDate(), e.getKey().getEndDate(), e.getValue()))
                .toList();
    }

    private List<BookingRange> mergeBookingRanges(List<BookingRange> ranges) {
        return rangeCalculator.mergeRanges(ranges).stream().toList();
    }

    @Override
    public List<RoomAvailability> calculateAvailability(String hotelId, String roomType, LocalDate startDate, LocalDate endDate) {
        HotelData hotelData = hotelsById.get(hotelId);
        if (hotelData == null) {
            throw new EntityNotFoundException("Hotel not found: " + hotelId);
        }

        Integer roomCount = hotelData.getRoomsByRoomType().get(roomType);
        if (roomCount == null) {
            throw new EntityNotFoundException("No rooms of selected type found. Room type: " + roomType + ", hotel " + hotelId);
        }

        List<BookingRange> bookingRanges = hotelData.getBookingsByRoomType().get(roomType);
        if (bookingRanges == null) {
            bookingRanges = List.of();
        }

        BookingRange searchRange = new BookingRange(startDate, endDate, roomCount);
        return rangeCalculator.subtractRanges(searchRange, bookingRanges).stream()
                .map(r -> new RoomAvailability(hotelData.getHotel(), roomType, r.getCount(), r.getStartDate(), r.getEndDate()))
                .toList();
    }
}
