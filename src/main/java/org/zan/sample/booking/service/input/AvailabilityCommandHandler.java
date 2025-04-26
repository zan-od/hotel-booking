package org.zan.sample.booking.service.input;

import org.springframework.stereotype.Component;
import org.zan.sample.booking.service.search.SearchService;

import java.time.LocalDate;

@Component
public class AvailabilityCommandHandler extends AbstractCommandHandler{
    public AvailabilityCommandHandler(SearchService searchService) {
        super(searchService);
    }

    @Override
    public AvailabilityCommand parseCommand(String command) {
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
