package com.example.HotelBooking.controller;

import com.example.HotelBooking.model.dto.RatePlanDto;
import com.example.HotelBooking.model.entities.RatePlan;
import com.example.HotelBooking.service.RatePlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/rateplans")
public class RatePlanController {

    @Autowired
    RatePlanService ratePlanService;

    // 🔹 1. Creare RatePlan
    @PostMapping
    public ResponseEntity<RatePlan> createRatePlan(@RequestBody RatePlanDto dto) {
        return ResponseEntity.ok(ratePlanService.createRatePlan(dto));
    }

    // 🔹 2. Listare toate RatePlan-urile
    @GetMapping
    public ResponseEntity<List<RatePlan>> getAllRatePlans() {
        return ResponseEntity.ok(ratePlanService.findAllRatePlans());
    }

    // 🔹 3. Căutare RatePlan după ID
    @GetMapping("/{id}")
    public ResponseEntity<RatePlan> getRatePlanById(@PathVariable Long id) {
        return ratePlanService.findRatePlanById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔹 4. Căutare RatePlan-uri după hotel
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<RatePlan>> getRatePlansByHotel(@PathVariable Long hotelId) {
        return ResponseEntity.ok(ratePlanService.findRatePlansByHotel(hotelId));
    }

    // 🔹 5. Căutare RatePlan-uri după tipul de cameră
    @GetMapping("/roomtype/{roomTypeId}")
    public ResponseEntity<List<RatePlan>> getRatePlansByRoomType(@PathVariable Long roomTypeId) {
        return ResponseEntity.ok(ratePlanService.findRatePlansByRoomType(roomTypeId));
    }

    // 🔹 6. Căutare RatePlan-uri active într-un interval
    @GetMapping("/active")
    public ResponseEntity<List<RatePlan>> getActiveRatePlans(
            @RequestParam LocalDate start,
            @RequestParam LocalDate end) {
        return ResponseEntity.ok(ratePlanService.findActiveRatePlans(start, end));
    }

    // 🔹 7. Actualizare RatePlan
    @PutMapping("/{id}")
    public ResponseEntity<RatePlan> updateRatePlan(@PathVariable Long id, @RequestBody RatePlanDto dto) {
        return ResponseEntity.ok(ratePlanService.updateRatePlan(id, dto));
    }

    // 🔹 8. Ștergere RatePlan
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRatePlan(@PathVariable Long id) {
        ratePlanService.deleteRatePlan(id);
        return ResponseEntity.noContent().build();
    }
}
