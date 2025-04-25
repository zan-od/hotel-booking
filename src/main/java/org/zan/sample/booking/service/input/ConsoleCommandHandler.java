package org.zan.sample.booking.service.input;

public interface ConsoleCommandHandler {
    boolean supports(String command);
    String handle(String command);
}
