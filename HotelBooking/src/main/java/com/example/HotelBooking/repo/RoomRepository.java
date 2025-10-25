package com.example.HotelBooking.repo;

import com.example.HotelBooking.model.entities.Room;
import com.example.HotelBooking.model.enums.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByHotelId(Long hotelId);

    List<Room> findByHotelIdAndStatus(Long hotelId, RoomStatus status);
}
