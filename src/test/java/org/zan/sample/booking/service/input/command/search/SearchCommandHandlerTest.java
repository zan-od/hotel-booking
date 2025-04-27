package org.zan.sample.booking.service.input.command.search;

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
import static org.zan.sample.booking.service.input.CommandUtils.formatDate;

class SearchCommandHandlerTest {

    private SearchService searchService;
    private SearchCommandHandler handler;

    @BeforeEach
    void setUp() {
        searchService = mock(SearchService.class);
        handler = new SearchCommandHandler(searchService);
    }

    @Test
    void handleCommand() {
        LocalDate date = LocalDate.now();

        Hotel hotel = new Hotel("H1", "Hotel", List.of());
        when(searchService.calculateAvailability(
                eq("H1"), eq("SGL"), eq(date), eq(date.plusDays(20))))
                .thenReturn(List.of(
                        new RoomAvailability(hotel, "SGL", 5, date, date.plusDays(5)),
                        new RoomAvailability(hotel, "SGL", -1, date.plusDays(6), date.plusDays(10)),
                        new RoomAvailability(hotel, "SGL", 0, date.plusDays(11), date.plusDays(19)),
                        new RoomAvailability(hotel, "SGL", 1, date.plusDays(20), date.plusDays(20))
                ));

        String result = handler.handle("Search(H1, 20, SGL)");

        String firstInterval = "(" + formatDate(date) + "-" + formatDate(date.plusDays(5)) + ", 5)";
        String lastInterval = "(" + formatDate(date.plusDays(20)) + "-" + formatDate(date.plusDays(20)) + ", 1)";

        assertEquals(firstInterval + ", " + lastInterval, result);
    }

    @Test
    void parseValidCommand() {
        String command = "Search(H1, 365, SGL)";
        SearchCommand result = handler.parseCommand(command);

        assertEquals("H1", result.getHotelId());
        assertEquals("SGL", result.getRoomType());
        assertEquals(365, result.getDays());
    }

    @Test
    void parseValidCommandZeroNumberOfDays() {
        String command = "Search(H1, 0, SGL)";
        SearchCommand result = handler.parseCommand(command);

        assertEquals("H1", result.getHotelId());
        assertEquals("SGL", result.getRoomType());
        assertEquals(0, result.getDays());
    }

    @Test
    void parseInvalidCommandIncorrectParameterCount() {
        String command = "Availability(H1, 365SGL)";

        assertThrows(IllegalArgumentException.class, () -> {
            handler.parseCommand(command);
        });
    }

    @Test
    void parseInvalidCommandInvalidNumberOfDays() {
        String command = "Availability(H1, a3, SGL)";

        assertThrows(IllegalArgumentException.class, () -> {
            handler.parseCommand(command);
        });
    }

    @Test
    void parseInvalidNegativeNumberOfDays() {
        String command = "Availability(H1, -5, SGL)";

        assertThrows(IllegalArgumentException.class, () -> {
            handler.parseCommand(command);
        });
    }
}