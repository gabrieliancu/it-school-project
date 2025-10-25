package com.example.HotelBooking.controller;

import com.example.HotelBooking.model.dto.ReservationDto;
import com.example.HotelBooking.model.dto.ReservationRequestDto;
import com.example.HotelBooking.model.dto.ReservationResponseDto;
import com.example.HotelBooking.model.entities.Reservation;
import com.example.HotelBooking.model.entities.Room;
import com.example.HotelBooking.model.enums.ReservationStatus;
import com.example.HotelBooking.repo.RoomRepository;
import com.example.HotelBooking.service.ReservationService;
import com.example.HotelBooking.service.implementation.ReservationServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    ReservationService reservationService;

    // ✅ POST – Creare rezervare nouă (stare inițială ONHOLD)
    @PostMapping
    public ResponseEntity<ReservationResponseDto> createReservation(@RequestBody ReservationRequestDto request) {
        return ResponseEntity.ok(reservationService.createReservation(request));
    }

    // ✅ POST – Confirmare rezervare existentă
    @PostMapping("/confirm/{id}")
    public ResponseEntity<ReservationResponseDto> confirm(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.confirmReservation(id));
    }

    // ✅ POST – Anulare rezervare existentă
    @PostMapping("/cancel/{id}")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        reservationService.cancelReservation(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ GET – Listare toate rezervările (pentru admin)
    @GetMapping
    public ResponseEntity<List<ReservationResponseDto>> getAll() {
        return ResponseEntity.ok(reservationService.findAllReservations());
    }

    // ✅ GET – Rezervările unui anumit utilizator
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReservationResponseDto>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(reservationService.findReservationsByUser(userId));
    }

    // ✅ GET – Detalii despre 0 rezervare specifică
    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.findById(id));
    }
}