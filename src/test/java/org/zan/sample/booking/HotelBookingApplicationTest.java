package org.zan.sample.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.zan.sample.booking.service.input.CommandUtils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class HotelBookingApplicationTest {

    @Autowired
    private HotelBookingRunner hotelBookingRunner;

    @Autowired
    private Scanner scanner;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        Scanner testScanner() {
            return mock(Scanner.class);
        }
    }

    @Test
    void noParameters() {
        List<String> output = catchSystemOut(() -> {
            hotelBookingRunner.run();
        });

        assertThat(getLast(output, 0)).contains("Error: Failed to load data");
    }

    @Test
    void exitCommand() {
        when(scanner.nextLine())
                .thenReturn(" ");

        List<String> output = catchSystemOut(() -> {
            hotelBookingRunner.run(
                    "--hotels",
                    "./src/test/resources/hotels.json",
                    "--bookings",
                    "./src/test/resources/bookings.json");
        });

        assertThat(output.size()).isEqualTo(1);
    }

    @Test
    void wrongCommand() {
        when(scanner.nextLine())
                .thenReturn("aaa")
                .thenReturn("");

        List<String> output = catchSystemOut(() -> {
            hotelBookingRunner.run(
                    "--hotels",
                    "./src/test/resources/hotels.json",
                    "--bookings",
                    "./src/test/resources/bookings.json");
        });

        assertThat(getLast(output, 1)).contains("Unknown command");
    }

    @Test
    void availabilityCommand() {
        when(scanner.nextLine())
                .thenReturn("Availability(H1, 20000101-20250101, SGL)")
                .thenReturn("");

        List<String> output = catchSystemOut(() -> {
            hotelBookingRunner.run(
                    "--hotels",
                    "./src/test/resources/hotels.json",
                    "--bookings",
                    "./src/test/resources/bookings.json");
        });

        assertThat(getLast(output, 1).trim())
                .isEqualTo("(20000101-20240901, 2), (20240902-20240905, 1), (20240906-20250101, 2)");
    }

    @Test
    void availabilityCommandZeroAvailabilityRange() {
        when(scanner.nextLine())
                .thenReturn("Availability(H2, 20240901-20240906, DBL)")
                .thenReturn("");

        List<String> output = catchSystemOut(() -> {
            hotelBookingRunner.run(
                    "--hotels",
                    "./src/test/resources/hotels.json",
                    "--bookings",
                    "./src/test/resources/bookings.json");
        });

        assertThat(getLast(output, 1).trim())
                .isEqualTo("(20240901-20240901, 1), (20240902-20240903, 0), (20240904-20240904, 1), (20240905-20240906, 2)");
    }

    @Test
    void searchCommand() {
        when(scanner.nextLine())
                .thenReturn("Search(H1, 0, SGL)")
                .thenReturn("");

        List<String> output = catchSystemOut(() -> {
            hotelBookingRunner.run(
                    "--hotels",
                    "./src/test/resources/hotels.json",
                    "--bookings",
                    "./src/test/resources/bookings.json");
        });

        String reportDate = CommandUtils.formatDate(LocalDate.now());
        assertThat(getLast(output, 1).trim())
                .isEqualTo("(" + reportDate+ "-" + reportDate+ ", 2)");
    }

    private List<String> catchSystemOut(Runnable runnable) {
        PrintStream originalOut = System.out;

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            System.setOut(new PrintStream(outputStream));
            runnable.run();
        } finally {
            System.setOut(originalOut);
        }

        return Arrays.stream(outputStream.toString().split("\n")).toList();
    }

    private String getLast(List<String> list, int pos) {
        return list.get(list.size() - (1+pos));
    }
}