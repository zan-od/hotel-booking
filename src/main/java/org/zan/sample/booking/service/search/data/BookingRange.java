package org.zan.sample.booking.service.search.data;

import java.time.LocalDate;
import java.util.Objects;

public class BookingRange {
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final int count;

    public BookingRange(LocalDate startDate, LocalDate endDate, int count) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }

        this.startDate = startDate;
        this.endDate = endDate;
        this.count = count;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public int getCount() {
        return count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingRange that = (BookingRange) o;
        return Objects.equals(startDate, that.startDate) && Objects.equals(endDate, that.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startDate, endDate);
    }

    @Override
    public String toString() {
        return "BookingRange{" +
                "startDate=" + startDate +
                ", endDate=" + endDate +
                ", count=" + count +
                '}';
    }
}
