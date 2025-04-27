package org.zan.sample.booking.service.input.command;

public interface ConsoleCommandHandler {
    boolean supports(String commandString);
    String handle(String commandString);
}
