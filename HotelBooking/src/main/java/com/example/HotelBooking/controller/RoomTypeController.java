package com.example.HotelBooking.controller;

import com.example.HotelBooking.model.dto.RoomTypeDto;
import com.example.HotelBooking.model.entities.RoomType;
import com.example.HotelBooking.service.RoomTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roomtypes")
public class RoomTypeController {

    @Autowired
    RoomTypeService roomTypeService;

    //  GET - toate tipurile de camere
    @GetMapping
    public ResponseEntity<List<RoomType>> getAllRoomTypes() {
        return ResponseEntity.ok(roomTypeService.findAllRoomTypes());
    }

    //  GET - tip de cameră după ID
    @GetMapping("/{id}")
    public ResponseEntity<RoomType> getRoomTypeById(@PathVariable Long id) {
        return roomTypeService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //  GET - tip de cameră după nume
    @GetMapping("/name/{name}")
    public ResponseEntity<RoomType> getRoomTypeByName(@PathVariable String name) {
        return roomTypeService.findByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //  GET - tipuri de camere cu capacitate minimă
    @GetMapping("/capacity/{minCapacity}")
    public ResponseEntity<List<RoomType>> getRoomTypesByCapacity(@PathVariable int minCapacity) {
        return ResponseEntity.ok(roomTypeService.findByCapacity(minCapacity));
    }

    //  POST - adăugare nou tip de cameră
    @PostMapping
    public ResponseEntity<RoomType> createRoomType(@RequestBody RoomTypeDto dto) {
        return ResponseEntity.ok(roomTypeService.createRoomType(dto));
    }

    //  PUT - actualizare tip cameră
    @PutMapping("/{id}")
    public ResponseEntity<RoomType> updateRoomType(@PathVariable Long id, @RequestBody RoomTypeDto dto) {
        return ResponseEntity.ok(roomTypeService.updateRoomType(id, dto));
    }

    //  DELETE - ștergere tip cameră
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoomType(@PathVariable Long id) {
        roomTypeService.deleteRoomType(id);
        return ResponseEntity.noContent().build();
    }
}