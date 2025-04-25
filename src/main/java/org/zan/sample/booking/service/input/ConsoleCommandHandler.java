package org.zan.sample.booking.service.input;

public interface ConsoleCommandHandler {
    boolean supports(String commandString);
    AvailabilityCommand parseCommand(String commandString);
    String handle(String commandString);
}
