package org.zan.sample.booking.service.search;

import org.junit.jupiter.api.Test;
import org.zan.sample.booking.service.search.data.BookingRange;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

class RangeCalculatorTest {

    private final RangeCalculator calculator = new RangeCalculator();
    private static final LocalDate BASE_DATE = LocalDate.of(2024, 12, 31);

    @Test
    void mergeZeroRanges() {
        Set<BookingRange> ranges = calculator.mergeRanges(List.of());

        assertEquals(Set.of(), ranges);
    }

    @Test
    void mergeSingleRange() {
        Set<BookingRange> ranges = calculator.mergeRanges(List.of(range(1, 5, 1)));

        assertEquals(Set.of(range(1, 5, 1)), ranges);
    }

    /**
     *  11111
     *        22
     *  --------
     *  11111 22
     */    @Test
    void mergeNonOverlappingRanges() {
        Set<BookingRange> ranges = calculator.mergeRanges(List.of(
                range(1, 5, 1),
                range(7, 8, 2)
        ));

        Set<BookingRange> expected = Set.of(
                range(1, 5, 1),
                range(7, 8, 2)
        );
        assertEquals(expected, ranges);
    }

    /**
     *  11111
     *       111
     *          22
     *            2222
     *  --------------
     *  11111111222222
     */
    @Test
    void mergeNeighbourRanges() {
        Set<BookingRange> ranges = calculator.mergeRanges(List.of(
                range(1, 5, 1),
                range(6, 8, 1),
                range(9, 10, 2),
                range(11, 14, 2)
        ));

        Set<BookingRange> expected = Set.of(
                range(1, 8, 1),
                range(9, 14, 2)
        );
        assertEquals(expected, ranges);
    }

    /**
     *  11111
     *  22222
     *  -----
     *  33333
     */
    @Test
    void mergeSameRanges() {
        Set<BookingRange> ranges = calculator.mergeRanges(List.of(
                range(1, 5, 1),
                range(1, 5, 2)
        ));

        Set<BookingRange> expected = Set.of(
                range(1, 5, 3)
        );
        assertEquals(expected, ranges);
    }

    /**
     *  11111
     *   222
     *  -----
     *  13331
     */
    @Test
    void mergeInnerOuterRanges() {
        Set<BookingRange> ranges = calculator.mergeRanges(List.of(
                range(1, 5, 1),
                range(2, 4, 2)
        ));

        Set<BookingRange> expected = Set.of(
                range(1, 1, 1),
                range(2, 4, 3),
                range(5, 5, 1)
        );
        assertEquals(expected, ranges);
    }

    /**
     *  11111
     *  222
     *  -----
     *  33311
     */
    @Test
    void mergeAlignedToStartRanges() {
        Set<BookingRange> ranges = calculator.mergeRanges(List.of(
                range(1, 5, 1),
                range(1, 3, 2)
        ));

        Set<BookingRange> expected = Set.of(
                range(1, 3, 3),
                range(4, 5, 1)
        );
        assertEquals(expected, ranges);
    }

    /**
     *  11111
     *    222
     *  -----
     *  11333
     */
    @Test
    void mergeAlignedToEndRanges() {
        Set<BookingRange> ranges = calculator.mergeRanges(List.of(
                range(1, 5, 1),
                range(3, 5, 2)
        ));

        Set<BookingRange> expected = Set.of(
                range(1, 2, 1),
                range(3, 5, 3)
        );
        assertEquals(expected, ranges);
    }

    /**
     *  1111 222  33333 3
     *    44444444444
     *  -----------------
     *  115546664477733 3
     */
    @Test
    void mergeRangeOverlapsSeveralRanges() {
        Set<BookingRange> ranges = calculator.mergeRanges(List.of(
                range(1, 4, 1),
                range(6, 8, 2),
                range(11, 15, 3),
                range(17, 17, 3),
                range(3, 13, 4)
        ));

        Set<BookingRange> expected = new TreeSet<>(Comparator.comparing(BookingRange::getStartDate));
        expected.addAll(List.of(
                range(1, 2, 1),
                range(3, 4, 5),
                range(5, 5, 4),
                range(6, 8, 6),
                range(9, 10, 4),
                range(11, 13, 7),
                range(14, 15, 3),
                range(17, 17, 3)
        ));
        assertEquals(expected, ranges);
    }

    private BookingRange range(int startDate, int endDate, int count) {
        return new BookingRange(day(startDate), day(endDate), count);
    }

    private LocalDate day(int number) {
        return BASE_DATE.plusDays(number);
    }
}