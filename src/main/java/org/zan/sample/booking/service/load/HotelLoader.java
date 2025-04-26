package org.zan.sample.booking.service.load;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.zan.sample.booking.service.load.dto.HotelDto;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class HotelLoader {
    private final ObjectMapper jsonMapper = new ObjectMapper();

    public List<HotelDto> loadHotels(String filePath) {
        try {
            return jsonMapper.readValue(new File(filePath), new TypeReference<List<HotelDto>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Error parsing hotel data: ", e);
        }
    }
}
