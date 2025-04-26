package org.zan.sample.booking;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.zan.sample.booking.service.input.ConsoleInputProcessor;
import org.zan.sample.booking.service.load.DataLoader;

import java.util.Scanner;

@Component
public class HotelBookingRunner implements CommandLineRunner {
    private final DataLoader dataLoader;
    private final ConsoleInputProcessor consoleInputProcessor;

    public HotelBookingRunner(DataLoader dataLoader, ConsoleInputProcessor consoleInputProcessor) {
        this.dataLoader = dataLoader;
        this.consoleInputProcessor = consoleInputProcessor;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!loadData(args)) {
            System.out.println("Error: Failed to load data");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        consoleInputProcessor.processInput(scanner);
    }

    private boolean loadData(String[] args) {
        String hotelDataFile = null;
        String bookingDataFile = null;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            boolean isLastArg = (i == args.length - 1);
            if (arg.equals("--hotels") && !isLastArg) {
                hotelDataFile = args[++i];
            } else if (arg.equals("--bookings") && !isLastArg) {
                bookingDataFile = args[++i];
            }
        }

        if (hotelDataFile == null) {
            System.out.println("Error: Missing --hotels argument");
            return false;
        }

        if (bookingDataFile == null) {
            System.out.println("Error: Missing --bookings argument");
            return false;
        }

        dataLoader.loadData(hotelDataFile, bookingDataFile);
        return true;
    }
}
