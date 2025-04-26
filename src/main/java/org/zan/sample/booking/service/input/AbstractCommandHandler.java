package org.zan.sample.booking.service.input;

import org.zan.sample.booking.model.Hotel;
import org.zan.sample.booking.model.RoomAvailability;
import org.zan.sample.booking.service.search.SearchService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractCommandHandler implements ConsoleCommandHandler {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    protected final SearchService searchService;

    public AbstractCommandHandler(SearchService searchService) {
        this.searchService = searchService;
    }

    @Override
    public String handle(String commandString) {
        AvailabilityCommand command = parseCommand(commandString);
        Hotel hotel = searchService.findHotel(command.getHotelId())
                .orElseThrow(() -> new IllegalArgumentException("Hotel not found: " + command.getHotelId()));

        List<RoomAvailability> availabilities = searchService.calculateAvailability(
                hotel, command.getRoomType(), command.getStartDate(), command.getEndDate());

        return formatAvailability(availabilities);
    }

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
