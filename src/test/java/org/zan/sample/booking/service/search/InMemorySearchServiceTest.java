package org.zan.sample.booking.service.search;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.zan.sample.booking.model.Booking;
import org.zan.sample.booking.model.Hotel;
import org.zan.sample.booking.model.Room;
import org.zan.sample.booking.model.RoomAvailability;
import org.zan.sample.booking.service.search.data.BookingRange;
import org.zan.sample.booking.service.search.exception.EntityNotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InMemorySearchServiceTest {

    private RangeCalculator rangeCalculator;
    private InMemorySearchService searchService;

    @BeforeEach
    void setUp() {
        rangeCalculator = mock(RangeCalculator.class);
        searchService = new InMemorySearchService(rangeCalculator);
    }

    @Test
    void clearData() {
        Hotel hotel = new Hotel("H1", "Hotel 1", List.of());
        searchService.loadData(List.of(hotel), List.of());
        searchService.clearData();

        LocalDate date = LocalDate.now();
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> searchService.calculateAvailability("H1", "SGL", date, date));
        assertTrue(ex.getMessage().contains("Hotel not found"));
    }

    @Test
    void loadData() {
        Hotel hotel = new Hotel("H1", "Hotel 1", List.of(new Room("1", "SGL")));
        LocalDate date = LocalDate.parse("2025-01-01");
        Booking booking = new Booking(hotel, "SGL", date, date);

        when(rangeCalculator.mergeRanges(eq(List.of(new BookingRange(date, date, 1)))))
                .thenReturn(Set.of(new BookingRange(date, date, 1)));
        when(rangeCalculator.subtractRanges(
                eq(new BookingRange(date, date, 1)),
                eq(List.of(new BookingRange(date, date, 1))))
        ).thenReturn(Set.of(new BookingRange(date, date, 0)));

        searchService.loadData(List.of(hotel), List.of(booking));

        List<RoomAvailability> result = searchService.calculateAvailability("H1", "SGL", date, date);
        assertEquals(1, result.size());
        assertEquals(hotel, result.get(0).getHotel());
        assertEquals("SGL", result.get(0).getRoomType());
        assertEquals(date, result.get(0).getStartDate());
        assertEquals(date, result.get(0).getEndDate());
        assertEquals(0, result.get(0).getRoomCount());
    }

    @Test
    void calculateAvailabilityHotelNotFound() {
        Hotel hotel = new Hotel("H1", "Hotel 1", List.of(new Room("1", "SGL")));
        LocalDate date = LocalDate.parse("2025-01-01");
        Booking booking = new Booking(hotel, "SGL", date, date);

        when(rangeCalculator.mergeRanges(eq(List.of(new BookingRange(date, date, 1)))))
                .thenReturn(Set.of(new BookingRange(date, date, 1)));
        when(rangeCalculator.subtractRanges(
                eq(new BookingRange(date, date, 1)),
                eq(List.of(new BookingRange(date, date, 1))))
        ).thenReturn(Set.of(new BookingRange(date, date, 1)));
        searchService.loadData(List.of(hotel), List.of(booking));

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> searchService.calculateAvailability("H2", "SGL", date, date));
        assertTrue(ex.getMessage().contains("Hotel not found"));
    }

    @Test
    void calculateAvailabilityRoomTypeNotFound() {
        Hotel hotel = new Hotel("H1", "Hotel 1", List.of(new Room("1", "SGL")));
        LocalDate date = LocalDate.parse("2025-01-01");
        Booking booking = new Booking(hotel, "SGL", date, date);

        when(rangeCalculator.mergeRanges(eq(List.of(new BookingRange(date, date, 1)))))
                .thenReturn(Set.of(new BookingRange(date, date, 1)));
        when(rangeCalculator.subtractRanges(
                eq(new BookingRange(date, date, 1)),
                eq(List.of(new BookingRange(date, date, 1))))
        ).thenReturn(Set.of(new BookingRange(date, date, 1)));
        searchService.loadData(List.of(hotel), List.of(booking));

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> searchService.calculateAvailability("H1", "DBL", date, date));
        assertTrue(ex.getMessage().contains("No rooms"));
    }

    @Test
    void calculateAvailabilityNoBookings() {
        Hotel hotel = new Hotel("H1", "Hotel 1", List.of(new Room("1", "SGL")));
        LocalDate date = LocalDate.parse("2025-01-01");

        when(rangeCalculator.mergeRanges(eq(List.of())))
                .thenReturn(Set.of());
        when(rangeCalculator.subtractRanges(
                eq(new BookingRange(date, date, 1)),
                eq(List.of()))
        ).thenReturn(Set.of(new BookingRange(date, date, 1)));
        searchService.loadData(List.of(hotel), List.of());

        List<RoomAvailability> result = searchService.calculateAvailability("H1", "SGL", date, date);
        assertEquals(1, result.size());
        assertEquals(hotel, result.get(0).getHotel());
        assertEquals("SGL", result.get(0).getRoomType());
        assertEquals(date, result.get(0).getStartDate());
        assertEquals(date, result.get(0).getEndDate());
        assertEquals(1, result.get(0).getRoomCount());
    }

    @Test
    void loadDataGroupSameBookings() {
        Hotel hotel = new Hotel("H1", "Hotel 1", List.of(new Room("1", "SGL")));
        LocalDate date = LocalDate.parse("2025-01-01");
        Booking booking = new Booking(hotel, "SGL", date, date);

        when(rangeCalculator.mergeRanges(eq(List.of(new BookingRange(date, date, 2)))))
                .thenReturn(Set.of(new BookingRange(date, date, 2)));
        when(rangeCalculator.subtractRanges(
                eq(new BookingRange(date, date, 1)),
                eq(List.of(new BookingRange(date, date, 2))))
        ).thenReturn(Set.of(new BookingRange(date, date, -1)));

        searchService.loadData(List.of(hotel), List.of(booking, booking));

        List<RoomAvailability> result = searchService.calculateAvailability("H1", "SGL", date, date);
        assertEquals(1, result.size());
        assertEquals(hotel, result.get(0).getHotel());
        assertEquals("SGL", result.get(0).getRoomType());
        assertEquals(date, result.get(0).getStartDate());
        assertEquals(date, result.get(0).getEndDate());
        assertEquals(-1, result.get(0).getRoomCount());
    }

    @Test
    void loadDataGroupByHotelAndRoomType() {
        Hotel hotel1 = new Hotel("H1", "Hotel 1",
                List.of(new Room("1", "SGL"), new Room("2", "DBL")));
        Hotel hotel2 = new Hotel("H2", "Hotel 2",
                List.of(new Room("1", "SGL"), new Room("2", "DBL"), new Room("3", "SGL")));
        LocalDate date = LocalDate.parse("2025-01-01");

        List<Booking> bookings = List.of(
                new Booking(hotel1, "SGL", date, date),
                new Booking(hotel1, "SGL", date, date),
                new Booking(hotel2, "SGL", date, date.plusDays(1)),
                new Booking(hotel2, "DBL", date, date.plusDays(2))
        );

        when(rangeCalculator.mergeRanges(eq(List.of(new BookingRange(date, date, 2)))))
                .thenReturn(Set.of(new BookingRange(date, date, 2)));
        when(rangeCalculator.mergeRanges(eq(List.of(new BookingRange(date, date.plusDays(1), 1)))))
                .thenReturn(Set.of(new BookingRange(date, date.plusDays(1), 1)));
        when(rangeCalculator.mergeRanges(eq(List.of(new BookingRange(date, date.plusDays(2), 1)))))
                .thenReturn(Set.of(new BookingRange(date, date.plusDays(2), 1)));

        searchService.loadData(List.of(hotel1, hotel2), bookings);

        // H1, SGL
        when(rangeCalculator.subtractRanges(
                eq(new BookingRange(date, date, 1)),
                eq(List.of(new BookingRange(date, date, 2))))
        ).thenReturn(Set.of(new BookingRange(date, date, -1)));

        List<RoomAvailability> result = searchService.calculateAvailability("H1", "SGL", date, date);
        assertEquals(1, result.size());
        assertEquals(hotel1, result.get(0).getHotel());
        assertEquals("SGL", result.get(0).getRoomType());
        assertEquals(date, result.get(0).getStartDate());
        assertEquals(date, result.get(0).getEndDate());
        assertEquals(-1, result.get(0).getRoomCount());

        // H1, DBL
        when(rangeCalculator.subtractRanges(
                eq(new BookingRange(date, date, 1)),
                eq(List.of()))
        ).thenReturn(Set.of(new BookingRange(date, date, 1)));

        result = searchService.calculateAvailability("H1", "DBL", date, date);
        assertEquals(1, result.size());
        assertEquals(hotel1, result.get(0).getHotel());
        assertEquals("DBL", result.get(0).getRoomType());
        assertEquals(date, result.get(0).getStartDate());
        assertEquals(date, result.get(0).getEndDate());
        assertEquals(1, result.get(0).getRoomCount());

        // H2, SGL
        when(rangeCalculator.subtractRanges(
                eq(new BookingRange(date, date, 1)),
                eq(List.of(new BookingRange(date, date.plusDays(1), 2))))
        ).thenReturn(Set.of(new BookingRange(date, date, -1)));

        result = searchService.calculateAvailability("H2", "SGL", date, date);
        assertEquals(1, result.size());
        assertEquals(hotel2, result.get(0).getHotel());
        assertEquals("SGL", result.get(0).getRoomType());
        assertEquals(date, result.get(0).getStartDate());
        assertEquals(date, result.get(0).getEndDate());
        assertEquals(-1, result.get(0).getRoomCount());

        // H2, DBL
        when(rangeCalculator.subtractRanges(
                eq(new BookingRange(date, date, 2)),
                eq(List.of(new BookingRange(date, date.plusDays(2), 1))))
        ).thenReturn(Set.of(new BookingRange(date, date, 1)));

        result = searchService.calculateAvailability("H2", "DBL", date, date);
        assertEquals(1, result.size());
        assertEquals(hotel2, result.get(0).getHotel());
        assertEquals("DBL", result.get(0).getRoomType());
        assertEquals(date, result.get(0).getStartDate());
        assertEquals(date, result.get(0).getEndDate());
        assertEquals(1, result.get(0).getRoomCount());
    }
}