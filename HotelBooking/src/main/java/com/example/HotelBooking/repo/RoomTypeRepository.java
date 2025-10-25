package com.example.HotelBooking.repo;

import com.example.HotelBooking.model.entities.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomTypeRepository extends JpaRepository<RoomType, Long> {
    public Optional<RoomType> findByName(String name);
    List<RoomType> findByCapacityGreaterThanEqual(int capacity);
}
