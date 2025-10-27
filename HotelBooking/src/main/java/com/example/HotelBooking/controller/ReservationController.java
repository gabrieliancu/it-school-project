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

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    //  POST - Creare rezervare
    @PostMapping
    public ResponseEntity<ReservationResponseDto> createReservation(@RequestBody ReservationRequestDto request) {
        return ResponseEntity.ok(reservationService.createReservation(request));
    }

    //  POST - Confirmare rezervare
    @PostMapping("/confirm/{id}")
    public ResponseEntity<ReservationResponseDto> confirmReservation(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.confirmReservation(id));
    }

    //  DELETE - Anulare rezervare
    @DeleteMapping("/cancel/{id}")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long id) {
        reservationService.cancelReservation(id);
        return ResponseEntity.noContent().build();
    }

    //  GET - Toate rezervările
    @GetMapping
    public ResponseEntity<List<ReservationResponseDto>> getAllReservations() {
        return ResponseEntity.ok(reservationService.findAllReservations());
    }

    //  GET - Rezervările unui utilizator
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReservationResponseDto>> getReservationsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(reservationService.findReservationsByUser(userId));
    }

    //  GET - O rezervare după ID
    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponseDto> getReservationById(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.findById(id));
    }
}