package org.zan.sample.booking.service.input;

import org.springframework.stereotype.Component;
import org.zan.sample.booking.service.input.command.ConsoleCommandHandler;
import org.zan.sample.booking.service.search.exception.EntityNotFoundException;

import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

@Component
public class ConsoleInputProcessor {

    private final Set<ConsoleCommandHandler> commandHandlers;

    public ConsoleInputProcessor(Set<ConsoleCommandHandler> commandHandlers) {
        this.commandHandlers = commandHandlers;
    }

    public void processInput(Scanner scanner) {
        while (true) {
            System.out.print("Enter command: \n");
            String input = scanner.nextLine();

            if (input.isBlank()) {
                break;
            }

            resolveHandler(input).ifPresentOrElse(
                    handler -> {
                        try {
                            System.out.println(handler.handle(input));
                        } catch (IllegalArgumentException | EntityNotFoundException e) {
                            System.out.println(e.getMessage());
                        }
                    },
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
