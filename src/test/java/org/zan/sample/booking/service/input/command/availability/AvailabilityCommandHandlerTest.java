package org.zan.sample.booking.service.input.command.availability;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.zan.sample.booking.model.Hotel;
import org.zan.sample.booking.model.RoomAvailability;
import org.zan.sample.booking.service.search.SearchService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AvailabilityCommandHandlerTest {

    private SearchService searchService;
    private AvailabilityCommandHandler handler;

    @BeforeEach
    void setUp() {
        searchService = mock(SearchService.class);
        handler = new AvailabilityCommandHandler(searchService);
    }

    @Test
    void handleCommand() {
        Hotel hotel = new Hotel("H1", "Hotel", List.of());
        when(searchService.calculateAvailability(
                eq("H1"), eq("SGL"), eq(LocalDate.parse("2025-01-01")), eq(LocalDate.parse("2025-01-07"))))
                .thenReturn(List.of(
                        new RoomAvailability(hotel, "SGL", 5, LocalDate.parse("2025-01-01"), LocalDate.parse("2025-01-05")),
                        new RoomAvailability(hotel, "SGL", -1, LocalDate.parse("2025-01-06"), LocalDate.parse("2025-01-06")),
                        new RoomAvailability(hotel, "SGL", 0, LocalDate.parse("2025-01-07"), LocalDate.parse("2025-01-07"))
                ));

        String result = handler.handle("Availability(H1, 20250101-20250107, SGL)");

        assertEquals("(20250101-20250105, 5), (20250106-20250106, -1), (20250107-20250107, 0)", result);
    }

    @Test
    void parseValidCommand() {
        String command = "Availability(H1, 20250112-20250201, SGL)";
        AvailabilityCommand result = handler.parseCommand(command);

        assertEquals("H1", result.getHotelId());
        assertEquals("SGL", result.getRoomType());
        assertEquals(LocalDate.parse("2025-01-12"), result.getStartDate());
        assertEquals(LocalDate.parse("2025-02-01"), result.getEndDate());
    }

    @Test
    void parseValidCommandSingleDate() {
        String command = "Availability(H1, 20250112, SGL)";
        AvailabilityCommand result = handler.parseCommand(command);

        assertEquals("H1", result.getHotelId());
        assertEquals("SGL", result.getRoomType());
        assertEquals(LocalDate.parse("2025-01-12"), result.getStartDate());
        assertEquals(LocalDate.parse("2025-01-12"), result.getEndDate());
    }

    @Test
    void parseInvalidCommandIncorrectParameterCount() {
        String command = "Availability(H1, 20250112SGL)";

        assertThrows(IllegalArgumentException.class, () -> {
            handler.parseCommand(command);
        });
    }

    @Test
    void parseInvalidCommandIncorrectDate() {
        String command = "Availability(H1, 202501.12, SGL)";

        assertThrows(IllegalArgumentException.class, () -> {
            handler.parseCommand(command);
        });
    }
}