package org.zan.sample.booking.service.input;

import org.junit.jupiter.api.Test;
import org.zan.sample.booking.model.Hotel;
import org.zan.sample.booking.model.RoomAvailability;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.zan.sample.booking.service.input.CommandUtils.*;

class CommandUtilsTest {

    @Test
    void formatEmptyAvailability() {
        String result = formatAvailability(List.of());
        assertEquals("", result);
    }

    @Test
    void formatSingleAvailability() {
        Hotel hotel = new Hotel("H1", "Hotel 1", List.of());
        String result = formatAvailability(List.of(
                new RoomAvailability(hotel, "SGL", 3, LocalDate.parse("2025-01-12"), LocalDate.parse("2025-01-15"))
        ));
        assertEquals("(20250112-20250115, 3)", result);
    }

    @Test
    void formatMultipleAvailabilities() {
        Hotel hotel = new Hotel("H1", "Hotel 1", List.of());
        String result = formatAvailability(List.of(
                new RoomAvailability(hotel, "SGL", 3, LocalDate.parse("2025-01-12"), LocalDate.parse("2025-01-15")),
                new RoomAvailability(hotel, "SGL", 2, LocalDate.parse("2025-01-17"), LocalDate.parse("2025-01-17"))
        ));
        assertEquals("(20250112-20250115, 3), (20250117-20250117, 2)", result);
    }
}