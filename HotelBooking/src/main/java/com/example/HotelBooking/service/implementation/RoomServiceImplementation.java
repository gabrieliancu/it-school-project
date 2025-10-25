package com.example.HotelBooking.service.implementation;

import com.example.HotelBooking.model.dto.RoomDto;
import com.example.HotelBooking.model.entities.Hotel;
import com.example.HotelBooking.model.entities.Room;
import com.example.HotelBooking.model.enums.RoomStatus;
import com.example.HotelBooking.repo.HotelRepository;
import com.example.HotelBooking.repo.RoomRepository;
import com.example.HotelBooking.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomServiceImplementation implements RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Override
    public Room createRoom(RoomDto dto) {
        Hotel hotel = hotelRepository.findById(dto.getHotelId())
                .orElseThrow(() -> new RuntimeException("Hotel not found"));

        Room room = new Room();
        room.setRoomNumber(dto.getRoomNumber());
        room.setFloor(dto.getFloor());
        room.setHotel(hotel);

        // conversie status din text (DTO) în enum
        if (dto.getStatus() != null) {
            room.setStatus(RoomStatus.valueOf(dto.getStatus().toUpperCase()));
        } else {
            room.setStatus(RoomStatus.AVAILABLE);
        }

        return roomRepository.save(room);
    }

    @Override
    public List<Room> findAllRooms() {
        return roomRepository.findAll();
    }

    @Override
    public Room findRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));
    }

    @Override
    public List<Room> findRoomsByHotel(Long hotelId) {
        return roomRepository.findByHotelId(hotelId);
    }

    @Override
    public List<Room> findRoomsByHotelAndStatus(Long hotelId, RoomStatus status) {
        return roomRepository.findByHotelIdAndStatus(hotelId, status);
    }

    @Override
    public Room updateRoom(Long id, RoomDto dto) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        if (dto.getRoomNumber() != null) room.setRoomNumber(dto.getRoomNumber());
        if (dto.getFloor() != null) room.setFloor(dto.getFloor());
        if (dto.getStatus() != null)
            room.setStatus(RoomStatus.valueOf(dto.getStatus().toUpperCase()));

        return roomRepository.save(room);
    }

    @Override
    public void deleteRoom(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new RuntimeException("Room not found");
        }
        roomRepository.deleteById(id);
    }
}