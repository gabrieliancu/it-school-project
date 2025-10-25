package com.example.HotelBooking.service;

import com.example.HotelBooking.model.dto.RoomDto;
import com.example.HotelBooking.model.entities.Room;
import com.example.HotelBooking.model.enums.RoomStatus;

import java.util.List;

public interface RoomService {

    Room createRoom(RoomDto dto);

    Room updateRoom(Long id, RoomDto dto);

    void deleteRoom(Long id);

    Room findRoomById(Long id);

    List<Room> findAllRooms();

    List<Room> findRoomsByHotel(Long hotelId);

    List<Room> findRoomsByHotelAndStatus(Long hotelId, RoomStatus status);
}
