package com.example.HotelBooking.service.implementation;

import com.example.HotelBooking.model.dto.RoomDto;
import com.example.HotelBooking.model.entities.Hotel;
import com.example.HotelBooking.model.entities.Room;
import com.example.HotelBooking.model.entities.RoomType;
import com.example.HotelBooking.model.enums.RoomStatus;
import com.example.HotelBooking.repo.HotelRepository;
import com.example.HotelBooking.repo.RoomRepository;
import com.example.HotelBooking.repo.RoomTypeRepository;
import com.example.HotelBooking.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoomServiceImplementation implements RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final RoomTypeRepository roomTypeRepository;

    @Autowired
    public RoomServiceImplementation(RoomRepository roomRepository,
                                     HotelRepository hotelRepository,
                                     RoomTypeRepository roomTypeRepository) {
        this.roomRepository = roomRepository;
        this.hotelRepository = hotelRepository;
        this.roomTypeRepository = roomTypeRepository;
    }

    // 🔹 Creare cameră nouă
    @Override
    public Room createRoom(RoomDto dto) {
        Hotel hotel = hotelRepository.findById(dto.getHotelId())
                .orElseThrow(() -> new RuntimeException("Hotel with ID " + dto.getHotelId() + " not found"));

        Room room = new Room();
        room.setRoomNumber(dto.getRoomNumber());
        room.setFloor(dto.getFloor());
        room.setHotel(hotel);
        room.setStatus(dto.getStatus() != null ? dto.getStatus() : RoomStatus.AVAILABLE);

        if (dto.getRoomTypeId() != null) {
            RoomType roomType = roomTypeRepository.findById(dto.getRoomTypeId())
                    .orElseThrow(() -> new RuntimeException("RoomType with ID " + dto.getRoomTypeId() + " not found"));
            room.setRoomType(roomType);
        }

        return roomRepository.save(room);
    }

    // 🔹 Actualizare cameră
    @Override
    public Room updateRoom(Long id, RoomDto dto) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room with ID " + id + " not found"));

        if (dto.getRoomNumber() != null)
            room.setRoomNumber(dto.getRoomNumber());

        if (dto.getFloor() != null)
            room.setFloor(dto.getFloor());

        if (dto.getStatus() != null)
            room.setStatus(dto.getStatus());

        if (dto.getHotelId() != null) {
            Hotel hotel = hotelRepository.findById(dto.getHotelId())
                    .orElseThrow(() -> new RuntimeException("Hotel with ID " + dto.getHotelId() + " not found"));
            room.setHotel(hotel);
        }

        if (dto.getRoomTypeId() != null) {
            RoomType roomType = roomTypeRepository.findById(dto.getRoomTypeId())
                    .orElseThrow(() -> new RuntimeException("RoomType with ID " + dto.getRoomTypeId() + " not found"));
            room.setRoomType(roomType);
        }

        return roomRepository.save(room);
    }

    // 🔹 Ștergere cameră
    @Override
    public void deleteRoom(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new RuntimeException("Room with ID " + id + " not found");
        }
        roomRepository.deleteById(id);
    }

    // 🔹 Toate camerele
    @Override
    public List<Room> findAllRooms() {
        return roomRepository.findAll();
    }

    // 🔹 Căutare cameră după ID
    @Override
    public Room findRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room with ID " + id + " not found"));
    }

    // 🔹 Toate camerele dintr-un hotel
    @Override
    public List<Room> findRoomsByHotel(Long hotelId) {
        return roomRepository.findByHotelId(hotelId);
    }

    // 🔹 Toate camerele dintr-un hotel după status
    @Override
    public List<Room> findRoomsByHotelAndStatus(Long hotelId, RoomStatus status) {
        return roomRepository.findByHotelIdAndStatus(hotelId, status);
    }
}