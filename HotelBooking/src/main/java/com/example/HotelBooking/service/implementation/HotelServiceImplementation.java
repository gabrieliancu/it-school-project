package com.example.HotelBooking.service.implementation;

import com.example.HotelBooking.model.dto.HotelDto;
import com.example.HotelBooking.model.entities.Hotel;
import com.example.HotelBooking.repo.HotelRepository;
import com.example.HotelBooking.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HotelServiceImplementation implements HotelService {

    @Autowired
    private HotelRepository hotelRepository;

    @Override
    public List<Hotel> findAllHotels() {
        return hotelRepository.findAll();
    }

    @Override
    public List<Hotel> findByLocation(String location) {
        return hotelRepository.findByLocation(location);
    }

    @Override
    public List<Hotel> findByRatingGreaterThanEqual(Double rating) {
        return hotelRepository.findByRatingGreaterThanEqual(rating);
    }

    @Override
    public Hotel createHotel(HotelDto dto) {
        return hotelRepository.save(toEntity(dto));
    }

    @Override
    public Hotel updateHotel(Long id, HotelDto dto) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel not found: "+ id));
        hotel.setName(dto.getName());
        hotel.setLocation(dto.getLocation());
        hotel.setRating(dto.getRating());
        return hotelRepository.save(hotel);
    }

    @Override
    public void deleteHotel(Long id) {
        if (!hotelRepository.existsById(id)) {
            throw new RuntimeException("Hotel not found");
        }
        hotelRepository.deleteById(id);
    }
}
