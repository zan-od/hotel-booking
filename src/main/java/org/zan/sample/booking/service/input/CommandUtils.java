package org.zan.sample.booking.service.input;

import org.zan.sample.booking.model.RoomAvailability;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

public class CommandUtils {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    public static String formatAvailability(List<RoomAvailability> availabilities) {
        return availabilities.stream()
                .map(availability -> String.format("(%s-%s, %d)",
                        formatDate(availability.getStartDate()),
                        formatDate(availability.getEndDate()),
                        availability.getRoomCount()))
                .collect(Collectors.joining(", "));
    }

    public static LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format - expected yyyyMMdd, found: " + date);
        }
    }

    public static String formatDate(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }
}
