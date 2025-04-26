package org.zan.sample.booking.service.search;

import org.zan.sample.booking.service.search.data.BookingRange;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class RangeCalculator {
    public Set<BookingRange> mergeRanges(List<BookingRange> ranges) {
        NavigableSet<BookingRange> mergedRanges = createRangeSet();
        if (ranges.isEmpty()) {
            return mergedRanges;
        }

        List<BookingRange> sortedRanges = getSorted(ranges);
        for (BookingRange range : sortedRanges) {
            addRange(mergedRanges, range);
        }

        return mergeNeighbourRanges(mergedRanges);
    }

    private static TreeSet<BookingRange> createRangeSet() {
        return new TreeSet<>(Comparator.comparing(BookingRange::getStartDate));
    }

    private List<BookingRange> getSorted(List<BookingRange> ranges) {
        List<BookingRange> sorted = new ArrayList<>(ranges);
        sorted.sort(Comparator.comparing(BookingRange::getStartDate));
        return sorted;
    }

    private void addRange(NavigableSet<BookingRange> mergedRanges, BookingRange range) {
        BookingRange previousRange = mergedRanges.lower(range);
        Set<BookingRange> tail = previousRange == null ? mergedRanges : mergedRanges.tailSet(previousRange, true);
        Set<BookingRange> overlappedRanges = tail.stream()
                .filter(r -> isOverlapping(r, range))
                .collect(Collectors.toSet());

        if (overlappedRanges.isEmpty()) {
            mergedRanges.add(range);
            return;
        }

        mergedRanges.removeAll(overlappedRanges);

        for (BookingRange overlappedRange : overlappedRanges) {
            LocalDate innerStartDate = overlappedRange.getStartDate();
            LocalDate innerEndDate = overlappedRange.getEndDate();

            // old head - counter doesn't change
            if (range.getStartDate().isAfter(overlappedRange.getStartDate())) {
                mergedRanges.add(new BookingRange(overlappedRange.getStartDate(), range.getStartDate().minusDays(1), overlappedRange.getCount()));
                innerStartDate = range.getStartDate();
            }

            // old tail - counter doesn't change
            if (range.getEndDate().isBefore(overlappedRange.getEndDate())) {
                mergedRanges.add(new BookingRange(range.getEndDate().plusDays(1), overlappedRange.getEndDate(),
                        overlappedRange.getCount()));
                innerEndDate = range.getEndDate();
            } else if (range.getEndDate().isAfter(overlappedRange.getEndDate())) {
                mergedRanges.add(new BookingRange(overlappedRange.getEndDate().plusDays(1), range.getEndDate(),
                        range.getCount()));
                innerEndDate = overlappedRange.getEndDate();
            }

            mergedRanges.add(new BookingRange(innerStartDate, innerEndDate, range.getCount() + overlappedRange.getCount()));
        }
    }

    private Set<BookingRange> mergeNeighbourRanges(NavigableSet<BookingRange> ranges) {
        Set<BookingRange> result = createRangeSet();

        BookingRange currentRange = null;
        for (BookingRange range : ranges) {
            if (currentRange == null) {
                currentRange = range;
                continue;
            }

            if (currentRange.getEndDate().plusDays(1).equals(range.getStartDate())
                    && currentRange.getCount() == range.getCount()) {
                currentRange = new BookingRange(currentRange.getStartDate(), range.getEndDate(), currentRange.getCount());
            } else {
                result.add(currentRange);
                currentRange = range;
            }
        }

        if (currentRange != null) {
            result.add(currentRange);
        }

        return result;
    }

    private boolean isOverlapping(BookingRange range1, BookingRange range2) {
        return !range1.getStartDate().isAfter(range2.getEndDate()) && !range1.getEndDate().isBefore(range2.getStartDate());
    }
}
