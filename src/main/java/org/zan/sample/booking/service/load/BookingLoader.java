package org.zan.sample.booking.service.load;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.zan.sample.booking.service.load.dto.BookingDto;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class BookingLoader {
    private final ObjectMapper jsonMapper = new ObjectMapper();

    public List<BookingDto> loadBookings(String filePath) {
        try {
            return jsonMapper.readValue(new File(filePath), new TypeReference<List<BookingDto>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Error parsing booking data: ", e);
        }
    }
}
