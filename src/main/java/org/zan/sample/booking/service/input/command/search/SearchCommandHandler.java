package org.zan.sample.booking.service.input.command.search;

import org.springframework.stereotype.Component;
import org.zan.sample.booking.model.RoomAvailability;
import org.zan.sample.booking.service.input.command.ConsoleCommandHandler;
import org.zan.sample.booking.service.search.SearchService;

import java.time.LocalDate;
import java.util.List;

import static org.zan.sample.booking.service.input.CommandUtils.formatAvailability;

@Component
public class SearchCommandHandler implements ConsoleCommandHandler {
    private final SearchService searchService;

    public SearchCommandHandler(SearchService searchService) {
        this.searchService = searchService;
    }

    @Override
    public String handle(String commandString) {
        SearchCommand command = parseCommand(commandString);

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(command.getDays());

        List<RoomAvailability> availabilities = searchService.calculateAvailability(
                command.getHotelId(), command.getRoomType(), startDate, endDate).stream()
                .filter(a -> a.getRoomCount() > 0)
                .toList();

        return formatAvailability(availabilities);
    }

    SearchCommand parseCommand(String command) {
        String parametersSubstring = command.substring(command.indexOf("(") + 1, command.length() - 1);

        String[] parameters = parametersSubstring.split(",");
        if (parameters.length != 3) {
            throw new IllegalArgumentException("Invalid command format - expected 3 parameters, found: " + parameters.length);
        }

        String hotelId = parameters[0].trim();
        String daysParameter = parameters[1].trim();
        String roomType = parameters[2].trim();

        int days;
        try {
            days = Integer.parseInt(daysParameter);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Incorrect parameter days: " + daysParameter + ". Must be a non-negative integer");
        }

        if (days < 0) {
            throw new IllegalArgumentException("Incorrect parameter days: " + daysParameter + ". Must be a non-negative integer");
        }

        return new SearchCommand(hotelId, roomType, days);
    }

    @Override
    public boolean supports(String command) {
        return command.toLowerCase().startsWith("search");
    }
}
