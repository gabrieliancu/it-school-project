package com.example.HotelBooking.controller;
import com.example.HotelBooking.model.dto.RoomDto;
import com.example.HotelBooking.model.entities.Room;
import com.example.HotelBooking.model.enums.RoomStatus;
import com.example.HotelBooking.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    // 🔹 1️⃣ Creare cameră nouă
    @PostMapping
    public ResponseEntity<Room> createRoom(@RequestBody RoomDto dto) {
        Room createdRoom = roomService.createRoom(dto);
        return ResponseEntity.ok(createdRoom);
    }

    // 🔹 2️⃣ Actualizare cameră existentă
    @PutMapping("/{id}")
    public ResponseEntity<Room> updateRoom(@PathVariable Long id, @RequestBody RoomDto dto) {
        Room updatedRoom = roomService.updateRoom(id, dto);
        return ResponseEntity.ok(updatedRoom);
    }

    // 🔹 3️⃣ Ștergere cameră
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.ok("Room with ID " + id + " was deleted successfully");
    }

    // 🔹 4️⃣ Toate camerele
    @GetMapping
    public ResponseEntity<List<Room>> getAllRooms() {
        return ResponseEntity.ok(roomService.findAllRooms());
    }

    // 🔹 5️⃣ Cameră după ID
    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoomById(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.findRoomById(id));
    }

    // 🔹 6️⃣ Camere după hotel
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<Room>> getRoomsByHotel(@PathVariable Long hotelId) {
        return ResponseEntity.ok(roomService.findRoomsByHotel(hotelId));
    }

    // 🔹 7️⃣ Camere după hotel și status (AVAILABLE / BOOKED / MAINTENANCE)
    @GetMapping("/hotel/{hotelId}/status/{status}")
    public ResponseEntity<List<Room>> getRoomsByHotelAndStatus(
            @PathVariable Long hotelId,
            @PathVariable RoomStatus status) {
        return ResponseEntity.ok(roomService.findRoomsByHotelAndStatus(hotelId, status));
    }
}
