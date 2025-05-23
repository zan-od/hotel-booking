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

        assertEquals(sortedSetOf(), ranges);
    }

    @Test
    void mergeSingleRange() {
        Set<BookingRange> ranges = calculator.mergeRanges(List.of(range(1, 5, 1)));

        assertEquals(sortedSetOf(range(1, 5, 1)), ranges);
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

        Set<BookingRange> expected = sortedSetOf(
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

        Set<BookingRange> expected = sortedSetOf(
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

        Set<BookingRange> expected = sortedSetOf(
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

        Set<BookingRange> expected = sortedSetOf(
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

        Set<BookingRange> expected = sortedSetOf(
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

        Set<BookingRange> expected = sortedSetOf(
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

        Set<BookingRange> expected = sortedSetOf(
                range(1, 2, 1),
                range(3, 4, 5),
                range(5, 5, 4),
                range(6, 8, 6),
                range(9, 10, 4),
                range(11, 13, 7),
                range(14, 15, 3),
                range(17, 17, 3)
        );
        assertEquals(expected, ranges);
    }

    /**
     *  1111  111111
     *    4444444
     *  -----------------
     *  115544555111
     */
    @Test
    void mergeRangeOverlapsSeveralRanges1() {
        Set<BookingRange> ranges = calculator.mergeRanges(List.of(
                range(1, 4, 1),
                range(7, 12, 1),
                range(3, 9, 4)
        ));

        Set<BookingRange> expected = sortedSetOf(
                range(1, 2, 1),
                range(3, 4, 5),
                range(5, 6, 4),
                range(7, 9, 5),
                range(10, 12, 1)
        );
        assertEquals(expected, ranges);
    }

    /**
     *  1234567890123456789012345
     *    111
     *    111111111111111111111
     *     111111
     *  -------------------------
     *    222111111111111111111
     *     111111
     *  -------------------------
     *    233222211111111111111
     */
    @Test
    void mergeThirdStartinginTheMiddleOfTwo() {
        Set<BookingRange> ranges = calculator.mergeRanges(List.of(
                range(3, 5, 1),
                range(3, 23, 1),
                range(4, 9, 1)
        ));
        Set<BookingRange> expected = sortedSetOf(
                range(3, 3, 2),
                range(4, 5, 3),
                range(6, 9, 2),
                range(10, 23, 1)
        );
        assertEquals(expected, ranges);
    }

    /** 123456789012
     *    2221111111
     *     111111
     *      11
     *  -------------------------
     *    2343222111
     */
    @Test
    void mergeThirdStartinginTheMiddleOfTwo1() {
        Set<BookingRange> ranges = calculator.mergeRanges(List.of(
                range(3, 5, 2),
                range(4, 9, 1),
                range(5, 6, 1),
                range(6, 12, 1)
        ));
        Set<BookingRange> expected = sortedSetOf(
                range(3, 3, 2),
                range(4, 4, 3),
                range(5, 5, 4),
                range(6, 6, 3),
                range(7, 9, 2),
                range(10, 12, 1)
        );
        assertEquals(expected, ranges);
    }

    @Test
    void subtractZeroRange() {
        Set<BookingRange> ranges = calculator.subtractRanges(
                range(1, 5, 10),
                List.of()
        );

        assertEquals(sortedSetOf(range(1, 5, 10)), ranges);
    }


    /**
     *   33333333333333333
     *  1111 222  33333 3
     *  ------------------
     *   22231113300000303
     */
    @Test
    void subtractSeveralRanges() {
        Set<BookingRange> ranges = calculator.subtractRanges(
                range(2, 18, 3),
                List.of(
                        range(1, 4, 1),
                        range(6, 8, 2),
                        range(11, 15, 3),
                        range(17, 17, 3)
                ));

        Set<BookingRange> expected = sortedSetOf(
                range(2, 4, 2),
                range(5, 5, 3),
                range(6, 8, 1),
                range(9, 10, 3),
                range(11, 15, 0),
                range(16, 16, 3),
                range(17, 17, 0),
                range(18, 18, 3)
        );
        assertEquals(expected, ranges);
    }

    /**
     *     33333333
     *  11  11 222  33333 3
     *  ------------------
     *     32231113
     */
    @Test
    void subtractSkipNonOverlappingRanges() {
        Set<BookingRange> ranges = calculator.subtractRanges(
                range(4, 11, 3),
                List.of(
                        range(1, 2, 1),
                        range(5, 6, 1),
                        range(8, 10, 2),
                        range(13, 17, 3),
                        range(19, 19, 3)
                ));

        Set<BookingRange> expected = sortedSetOf(
                range(4, 4, 3),
                range(5, 6, 2),
                range(7, 7, 3),
                range(8, 10, 1),
                range(11, 11, 3)
        );
        assertEquals(expected, ranges);
    }

    /**
     *     333
     *  11    44
     *  ------------------
     *     333
     */
    @Test
    void subtractNonOverlappingRanges() {
        Set<BookingRange> ranges = calculator.subtractRanges(
                range(4, 6, 3),
                List.of(
                        range(1, 2, 1),
                        range(7, 8, 4)
                ));

        Set<BookingRange> expected = sortedSetOf(
                range(4, 6, 3)
        );
        assertEquals(expected, ranges);
    }

    @Test
    void subtractFromBigRange() {
        Set<BookingRange> ranges = calculator.subtractRanges(
                range(1, 1000, 3),
                List.of(
                        range(1, 5, 1),
                        range(100, 150, 2),
                        range(700, 800, 3)
                ));

        Set<BookingRange> expected = sortedSetOf(
                range(1, 5, 2),
                range(6, 99, 3),
                range(100, 150, 1),
                range(151, 699, 3),
                range(700, 800, 0),
                range(801, 1000, 3)
        );
        assertEquals(expected, ranges);
    }

    private Set<BookingRange> sortedSetOf(BookingRange... ranges) {
        Set<BookingRange> sortedSet = new TreeSet<>(Comparator.comparing(BookingRange::getStartDate).thenComparing(BookingRange::getEndDate));
        sortedSet.addAll(List.of(ranges));
        return sortedSet;
    }

    private BookingRange range(int startDate, int endDate, int count) {
        return new BookingRange(day(startDate), day(endDate), count);
    }

    private LocalDate day(int number) {
        return BASE_DATE.plusDays(number);
    }
}