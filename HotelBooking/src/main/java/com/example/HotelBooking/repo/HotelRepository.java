package com.example.HotelBooking.repo;

import com.example.HotelBooking.model.entities.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HotelRepository extends JpaRepository<Hotel, Long> {
    List<Hotel> findByLocation(String location);
    List<Hotel> findByRatingGreaterThanEqual(Double rating);
}
