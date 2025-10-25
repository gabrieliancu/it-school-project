package com.example.HotelBooking.controller;

import com.example.HotelBooking.model.dto.RoomDto;
import com.example.HotelBooking.model.entities.Room;
import com.example.HotelBooking.model.enums.RoomStatus;
import com.example.HotelBooking.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;

    // 🔹 GET - toate camerele
    @GetMapping
    public ResponseEntity<List<Room>> getAllRooms() {
        return ResponseEntity.ok(roomService.findAllRooms());
    }

    // 🔹 GET - cameră după ID
    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoomById(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.findRoomById(id));
    }

    // 🔹 GET - camerele unui hotel
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<Room>> getRoomsByHotel(@PathVariable Long hotelId) {
        return ResponseEntity.ok(roomService.findRoomsByHotel(hotelId));
    }

    // 🔹 GET - camerele unui hotel după status (AVAILABLE, BOOKED etc.)
    @GetMapping("/hotel/{hotelId}/status/{status}")
    public ResponseEntity<List<Room>> getRoomsByHotelAndStatus(
            @PathVariable Long hotelId,
            @PathVariable RoomStatus status) {
        return ResponseEntity.ok(roomService.findRoomsByHotelAndStatus(hotelId, status));
    }

    // 🔹 POST - creare cameră
    @PostMapping
    public ResponseEntity<Room> createRoom(@RequestBody RoomDto dto) {
        return ResponseEntity.ok(roomService.createRoom(dto));
    }

    // 🔹 PUT - actualizare cameră
    @PutMapping("/{id}")
    public ResponseEntity<Room> updateRoom(@PathVariable Long id, @RequestBody RoomDto dto) {
        return ResponseEntity.ok(roomService.updateRoom(id, dto));
    }

    // 🔹 DELETE - ștergere cameră
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }
}
