package org.zan.sample.booking.service.input;

import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

public class ConsoleInputProcessor {

    private final Set<ConsoleCommandHandler> commandHandlers;

    public ConsoleInputProcessor(Set<ConsoleCommandHandler> commandHandlers) {
        this.commandHandlers = commandHandlers;
    }

    public void processInput(Scanner scanner) {
        while (true) {
            System.out.print("Enter command: ");
            String input = scanner.nextLine();

            if (input.isBlank()) {
                break;
            }

            resolveHandler(input).ifPresentOrElse(
                    handler -> System.out.println(handler.handle(input)),
                    () -> System.out.println("Unknown command: " + input)
            );
        }
    }

    private Optional<ConsoleCommandHandler> resolveHandler(String command) {
        for (ConsoleCommandHandler handler : commandHandlers) {
            if (handler.supports(command)) {
                return Optional.of(handler);
            }
        }

        return Optional.empty();
    }
}
