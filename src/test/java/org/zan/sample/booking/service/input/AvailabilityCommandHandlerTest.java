package org.zan.sample.booking.service.input;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.zan.sample.booking.model.Hotel;
import org.zan.sample.booking.model.RoomAvailability;
import org.zan.sample.booking.service.search.SearchService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AvailabilityCommandHandlerTest {

    @Mock
    private SearchService searchService;
    private AvailabilityCommandHandler handler;

    @BeforeEach
    void setUp() {
        handler = new AvailabilityCommandHandler(searchService);
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

    @Test
    void formatEmptyAvailability() {
        String result = AvailabilityCommandHandler.formatAvailability(List.of());
        assertEquals("", result);
    }

    @Test
    void formatSingleAvailability() {
        Hotel hotel = new Hotel("H1", "Hotel 1", List.of());
        String result = AvailabilityCommandHandler.formatAvailability(List.of(
                new RoomAvailability(hotel, "SGL", 3, LocalDate.parse("2025-01-12"), LocalDate.parse("2025-01-15"))
        ));
        assertEquals("(20250112-20250115, 3)", result);
    }

    @Test
    void formatMultipleAvailabilities() {
        Hotel hotel = new Hotel("H1", "Hotel 1", List.of());
        String result = AvailabilityCommandHandler.formatAvailability(List.of(
                new RoomAvailability(hotel, "SGL", 3, LocalDate.parse("2025-01-12"), LocalDate.parse("2025-01-15")),
                new RoomAvailability(hotel, "SGL", 2, LocalDate.parse("2025-01-17"), LocalDate.parse("2025-01-17"))
        ));
        assertEquals("(20250112-20250115, 3), (20250117-20250117, 2)", result);
    }
}