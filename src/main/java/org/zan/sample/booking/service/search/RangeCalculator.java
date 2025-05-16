package org.zan.sample.booking.service.search;

import org.springframework.stereotype.Service;
import org.zan.sample.booking.service.search.data.BookingRange;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
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

    public Set<BookingRange> subtractRanges(BookingRange totalRange, List<BookingRange> ranges) {
        Set<BookingRange> result = createRangeSet();
        if (ranges.isEmpty()) {
            result.add(totalRange);
            return result;
        }

        List<BookingRange> sortedRanges = getSorted(ranges);

        BookingRange previousRange = null;
        for (BookingRange range : sortedRanges) {
            if (!isOverlapping(range, totalRange)) {
                continue;
            }

            if (previousRange == null) {
                if (totalRange.getStartDate().isBefore(range.getStartDate())) {
                    result.add(new BookingRange(totalRange.getStartDate(), range.getStartDate().minusDays(1), totalRange.getCount()));
                }
            }

            LocalDate startDate = totalRange.getStartDate().isAfter(range.getStartDate()) ? totalRange.getStartDate() : range.getStartDate();
            LocalDate endDate = totalRange.getEndDate().isBefore(range.getEndDate()) ? totalRange.getEndDate() : range.getEndDate();

            result.add(new BookingRange(startDate, endDate, totalRange.getCount() - range.getCount()));

            if (previousRange != null) {
                if (previousRange.getEndDate().plusDays(1).isBefore(range.getStartDate())) {
                    result.add(new BookingRange(
                            previousRange.getEndDate().plusDays(1),
                            range.getStartDate().minusDays(1),
                            totalRange.getCount()));
                }
            }
            previousRange = range;
        }

        if (previousRange != null) {
            if (totalRange.getEndDate().isAfter(previousRange.getEndDate())) {
                result.add(new BookingRange(previousRange.getEndDate().plusDays(1), totalRange.getEndDate(), totalRange.getCount()));
            }
        } else {
            // no overlapping ranges
            result.add(totalRange);
        }

        return result;
    }

    private static TreeSet<BookingRange> createRangeSet() {
        return new TreeSet<>(Comparator.comparing(BookingRange::getStartDate));
    }

    private List<BookingRange> getSorted(List<BookingRange> ranges) {
        List<BookingRange> sorted = new ArrayList<>(ranges);
        sorted.sort(Comparator.comparing(BookingRange::getStartDate).thenComparing(BookingRange::getEndDate));
        return sorted;
    }

    private void addRange(NavigableSet<BookingRange> mergedRanges, BookingRange range) {
        Set<BookingRange> overlappedRanges = filterOverlappingRanges(mergedRanges, range);

        if (overlappedRanges.isEmpty()) {
            mergedRanges.add(range);
            return;
        }

        mergedRanges.removeAll(overlappedRanges);

        for (BookingRange overlappedRange : overlappedRanges) {
            mergedRanges.addAll(mergeRanges(overlappedRange, range));
            //System.out.println(mergedRanges);
        }
    }

    private Set<BookingRange> filterOverlappingRanges(NavigableSet<BookingRange> mergedRanges, BookingRange range) {
        BookingRange previousRange = mergedRanges.lower(range);
        Set<BookingRange> tail = previousRange == null ? mergedRanges : mergedRanges.tailSet(previousRange, true);
        return tail.stream()
                .filter(r -> isOverlapping(r, range))
                .collect(Collectors.toSet());
    }

    private List<BookingRange> mergeRanges(BookingRange firstRange, BookingRange secondRange) {
        LocalDate innerStartDate = firstRange.getStartDate();
        LocalDate innerEndDate = firstRange.getEndDate();
        List<BookingRange> result = new ArrayList<>();

        // first or second range head - counter doesn't change
        if (secondRange.getStartDate().isAfter(firstRange.getStartDate())) {
            result.add(new BookingRange(firstRange.getStartDate(), secondRange.getStartDate().minusDays(1), firstRange.getCount()));
            innerStartDate = secondRange.getStartDate();
        } else if (secondRange.getStartDate().isBefore(firstRange.getStartDate())) {
            result.add(new BookingRange(secondRange.getStartDate(), firstRange.getStartDate().minusDays(1), secondRange.getCount()));
            innerStartDate = firstRange.getStartDate();
        }

        // first or second range tail - counter doesn't change
        if (secondRange.getEndDate().isBefore(firstRange.getEndDate())) {
            result.add(new BookingRange(secondRange.getEndDate().plusDays(1), firstRange.getEndDate(),
                    firstRange.getCount()));
            innerEndDate = secondRange.getEndDate();
        } else if (secondRange.getEndDate().isAfter(firstRange.getEndDate())) {
            result.add(new BookingRange(firstRange.getEndDate().plusDays(1), secondRange.getEndDate(),
                    secondRange.getCount()));
            innerEndDate = firstRange.getEndDate();
        }

        result.add(new BookingRange(innerStartDate, innerEndDate, secondRange.getCount() + firstRange.getCount()));

        return result;
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
