package com.example.HotelBooking.service.implementation;

import com.example.HotelBooking.model.dto.ReservationDto;
import com.example.HotelBooking.model.dto.ReservationRequestDto;
import com.example.HotelBooking.model.dto.ReservationResponseDto;
import com.example.HotelBooking.model.entities.RatePlan;
import com.example.HotelBooking.model.entities.Reservation;
import com.example.HotelBooking.model.entities.Room;
import com.example.HotelBooking.model.entities.User;
import com.example.HotelBooking.model.enums.ReservationStatus;
import com.example.HotelBooking.model.enums.RoomStatus;
import com.example.HotelBooking.repo.RatePlanRepository;
import com.example.HotelBooking.repo.ReservationRepository;
import com.example.HotelBooking.repo.RoomRepository;
import com.example.HotelBooking.repo.UserRepository;
import com.example.HotelBooking.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReservationServiceImplementation implements ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RatePlanRepository ratePlanRepository;

    // 🔹 1️⃣ Creare rezervare
    @Override
    public ReservationResponseDto createReservation(ReservationRequestDto request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        RatePlan ratePlan = ratePlanRepository.findById(request.getRatePlanId())
                .orElseThrow(() -> new RuntimeException("Rate plan not found"));

        Set<Room> rooms = new HashSet<>(roomRepository.findAllById(request.getRoomIds()));
        if (rooms.isEmpty())
            throw new RuntimeException("No rooms selected");

        // ✅ verificare conflicte
        List<Reservation> conflicts = reservationRepository.findConflictingReservations(
                request.getRoomIds(), request.getCheckInDate(), request.getCheckOutDate());

        if (!conflicts.isEmpty())
            throw new RuntimeException("Some rooms are already booked for this period");

        // 💰 calcul total
        long nights = request.getCheckOutDate().toEpochDay() - request.getCheckInDate().toEpochDay();
        BigDecimal total = ratePlan.getPricePerNight().multiply(BigDecimal.valueOf(nights));

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setRatePlan(ratePlan);
        reservation.setRooms(rooms);
        reservation.setCheckInDate(request.getCheckInDate());
        reservation.setCheckOutDate(request.getCheckOutDate());
        reservation.setTotalAmount(total);
        reservation.setStatus(ReservationStatus.ONHOLD);
        reservation.setCreatedAt(LocalDateTime.now());

        return toResponseDto(reservationRepository.save(reservation));
    }

    // 🔹 2️⃣ Confirmare rezervare
    @Override
    public ReservationResponseDto confirmReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        reservation.setStatus(ReservationStatus.CONFIRMED);

        // Marchează camerele ca BOOKED
        reservation.getRooms().forEach(room -> room.setStatus(RoomStatus.BOOKED));
        roomRepository.saveAll(reservation.getRooms());

        return toResponseDto(reservationRepository.save(reservation));
    }

    // 🔹 3️⃣ Anulare rezervare
    @Override
    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
    }

    // 🔹 4️⃣ Toate rezervările (admin)
    @Override
    public List<ReservationResponseDto> findAllReservations() {
        return reservationRepository.findAll()
                .stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    // 🔹 5️⃣ Rezervările unui utilizator
    @Override
    public List<ReservationResponseDto> findReservationsByUser(Long userId) {
        return reservationRepository.findByUserId(userId)
                .stream()
                .map(this::toResponseDto)
                .collect(Collectors.toList());
    }

    // 🔹 6️⃣ Căutare rezervare după ID
    @Override
    public ReservationResponseDto findById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        return toResponseDto(reservation);
    }

    // 🔹 7️⃣ Mapper entitate → DTO
    private ReservationResponseDto toResponseDto(Reservation reservation) {
        ReservationResponseDto dto = new ReservationResponseDto();
        dto.setReservationId(reservation.getId());
        dto.setStatus(reservation.getStatus().name());
        dto.setCreatedAt(reservation.getCreatedAt());
        dto.setTotalAmount(reservation.getTotalAmount());
        dto.setCheckInDate(reservation.getCheckInDate());
        dto.setCheckOutDate(reservation.getCheckOutDate());
        return dto;
    }
}