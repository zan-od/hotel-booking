package org.zan.sample.booking.service.load;

import org.zan.sample.booking.model.Booking;

import java.util.List;

public interface BookingLoader {
    List<Booking> loadHotels();
}
