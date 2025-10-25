package com.example.HotelBooking.controller;

import com.example.HotelBooking.model.dto.HotelDto;
import com.example.HotelBooking.model.entities.Hotel;
import com.example.HotelBooking.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    @Autowired
    HotelService hotelService;

    // ✅ GET – toate hotelurile
    @GetMapping
    public ResponseEntity<List<Hotel>> getAllHotels() {
        return ResponseEntity.ok(hotelService.findAllHotels());
    }

    // ✅ GET – hoteluri dintr-o locație
    @GetMapping("/location/{location}")
    public ResponseEntity<List<Hotel>> getHotelsByLocation(@PathVariable String location) {
        return ResponseEntity.ok(hotelService.findByLocation(location));
    }

    // ✅ GET – hoteluri cu rating minim
    @GetMapping("/rating/{minRating}")
    public ResponseEntity<List<Hotel>> getHotelsByRating(@PathVariable Double minRating) {
        return ResponseEntity.ok(hotelService.findByRatingGreaterThanEqual(minRating));
    }

    // ✅ POST – adăugare hotel nou
    @PostMapping
    public ResponseEntity<Hotel> createHotel(@RequestBody HotelDto dto) {
        Hotel created = hotelService.createHotel(dto);
        return ResponseEntity.ok(created);
    }

    // ✅ PUT – actualizare hotel
    @PutMapping("/{id}")
    public ResponseEntity<Hotel> updateHotel(@PathVariable Long id, @RequestBody HotelDto dto) {
        return ResponseEntity.ok(hotelService.updateHotel(id, dto));
    }

    // ✅ DELETE – ștergere hotel
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHotel(@PathVariable Long id) {
        hotelService.deleteHotel(id);
        return ResponseEntity.noContent().build();
    }
}
