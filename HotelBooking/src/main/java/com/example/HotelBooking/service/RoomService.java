package com.example.HotelBooking.service;

import com.example.HotelBooking.model.dto.RoomDto;
import com.example.HotelBooking.model.entities.Room;
import com.example.HotelBooking.model.enums.RoomStatus;

import java.util.List;

public interface RoomService {

    Room createRoom(RoomDto dto);

    List<Room> findAllRooms();

    Room findRoomById(Long id);

    List<Room> findRoomsByHotel(Long hotelId);

    List<Room> findRoomsByHotelAndStatus(Long hotelId, RoomStatus status);

    Room updateRoom(Long id, RoomDto dto);

    void deleteRoom(Long id);
}
