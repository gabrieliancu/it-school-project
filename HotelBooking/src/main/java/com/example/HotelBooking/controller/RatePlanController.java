package com.example.HotelBooking.controller;

import com.example.HotelBooking.model.dto.RatePlanDto;
import com.example.HotelBooking.model.entities.RatePlan;
import com.example.HotelBooking.service.RatePlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/rateplans")
public class RatePlanController {

    @Autowired
    private RatePlanService ratePlanService;

    //  Creare plan tarifar nou
    @PostMapping
    public ResponseEntity<RatePlan> createRatePlan(@RequestBody RatePlanDto dto) {
        RatePlan created = ratePlanService.createRatePlan(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    //  Toate planurile
    @GetMapping
    public ResponseEntity<List<RatePlan>> getAllRatePlans() {
        return ResponseEntity.ok(ratePlanService.findAllRatePlans());
    }

    //  Căutare după ID
    @GetMapping("/{id}")
    public ResponseEntity<RatePlan> getRatePlanById(@PathVariable Long id) {
        return ratePlanService.findRatePlanById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //  Căutare planuri după hotel
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<RatePlan>> getRatePlansByHotel(@PathVariable Long hotelId) {
        return ResponseEntity.ok(ratePlanService.findRatePlansByHotel(hotelId));
    }

    //  Căutare planuri după tipul de cameră
    @GetMapping("/roomtype/{roomTypeId}")
    public ResponseEntity<List<RatePlan>> getRatePlansByRoomType(@PathVariable Long roomTypeId) {
        return ResponseEntity.ok(ratePlanService.findRatePlansByRoomType(roomTypeId));
    }

    //  Filtrare planuri active într-o perioadă (toate hotelurile)
    @GetMapping("/active")
    public ResponseEntity<List<RatePlan>> getActiveRatePlans(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(ratePlanService.findActiveRatePlans(startDate, endDate));
    }

    // Actualizare plan tarifar
    @PutMapping("/{id}")
    public ResponseEntity<RatePlan> updateRatePlan(@PathVariable Long id, @RequestBody RatePlanDto dto) {
        return ResponseEntity.ok(ratePlanService.updateRatePlan(id, dto));
    }

    //  Ștergere plan tarifar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRatePlan(@PathVariable Long id) {
        ratePlanService.deleteRatePlan(id);
        return ResponseEntity.noContent().build();
    }
}