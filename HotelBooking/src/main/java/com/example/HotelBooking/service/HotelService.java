package com.example.HotelBooking.service;

import com.example.HotelBooking.model.dto.HotelDto;
import com.example.HotelBooking.model.entities.Hotel;

import java.util.List;
import java.util.Optional;

public interface HotelService {
    List<Hotel> findAllHotels();

    List<Hotel> findByLocation(String location);

    List<Hotel> findByRatingGreaterThanEqual(Double rating);

    Hotel createHotel(HotelDto dto);

    Hotel updateHotel(Long id, HotelDto dto);

    void deleteHotel(Long id);

    default Hotel toEntity(HotelDto dto) {
        Hotel hotel = new Hotel();
        hotel.setName(dto.getName());
        hotel.setLocation(dto.getLocation());
        hotel.setRating(dto.getRating());
        return hotel;
    }
}
