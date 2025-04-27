package org.zan.sample.booking.service.input.command.availability;

import org.springframework.stereotype.Component;
import org.zan.sample.booking.model.RoomAvailability;
import org.zan.sample.booking.service.input.command.ConsoleCommandHandler;
import org.zan.sample.booking.service.search.SearchService;

import java.time.LocalDate;
import java.util.List;

import static org.zan.sample.booking.service.input.CommandUtils.*;

@Component
public class AvailabilityCommandHandler implements ConsoleCommandHandler {
    private final SearchService searchService;

    public AvailabilityCommandHandler(SearchService searchService) {
        this.searchService = searchService;
    }

    @Override
    public String handle(String commandString) {
        AvailabilityCommand command = parseCommand(commandString);

        List<RoomAvailability> availabilities = searchService.calculateAvailability(
                command.getHotelId(), command.getRoomType(), command.getStartDate(), command.getEndDate());

        return formatAvailability(availabilities);
    }

    AvailabilityCommand parseCommand(String command) {
        String parametersSubstring = command.substring(command.indexOf("(") + 1, command.length() - 1);

        String[] parameters = parametersSubstring.split(",");
        if (parameters.length != 3) {
            throw new IllegalArgumentException("Invalid command format - expected 3 parameters, found: " + parameters.length);
        }

        String hotelId = parameters[0].trim();
        String dateParameter = parameters[1].trim();
        String roomType = parameters[2].trim();

        LocalDate startDate;
        LocalDate endDate;
        if (dateParameter.contains("-")) {
            String[] dates = dateParameter.split("-");
            if (dates.length != 2) {
                throw new IllegalArgumentException("Invalid date format - expected start and end date, found dates: " + dates.length);
            }
            startDate = parseDate(dates[0].trim());
            endDate = parseDate(dates[1].trim());
        } else {
            startDate = parseDate(dateParameter);
            endDate = startDate;
        }

        return new AvailabilityCommand(hotelId, roomType, startDate, endDate);
    }

    @Override
    public boolean supports(String command) {
        return command.toLowerCase().startsWith("availability");
    }
}
