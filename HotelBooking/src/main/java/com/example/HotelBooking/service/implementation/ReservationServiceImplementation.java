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
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReservationServiceImplementation implements ReservationService {

    @Autowired private ReservationRepository reservationRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private RoomRepository roomRepository;
    @Autowired private RatePlanRepository ratePlanRepository;

    // 🔹 Creare rezervare nouă (cu suport pentru mai multe planuri tarifare)
    @Override
    public ReservationResponseDto createReservation(ReservationRequestDto request) {

        // ✅ verifică existența user-ului
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ verifică camerele selectate
        Set<Room> rooms = new HashSet<>(roomRepository.findAllById(request.getRoomIds()));
        if (rooms.isEmpty()) {
            throw new RuntimeException("No rooms selected");
        }

        Room sampleRoom = rooms.iterator().next();

        // ✅ obține toate planurile tarifare pentru hotel și tipul de cameră
        List<RatePlan> ratePlans = ratePlanRepository.findAllByHotelIdAndRoomTypeId(
                sampleRoom.getHotel().getId(),
                sampleRoom.getRoomType().getId()
        );

        if (ratePlans.isEmpty()) {
            throw new RuntimeException("No rate plans found for this room type");
        }

        // ✅ verifică conflictele cu alte rezervări confirmate
        List<Reservation> conflicts = reservationRepository.findConflictingReservations(
                new ArrayList<>(request.getRoomIds()),
                request.getCheckInDate(),
                request.getCheckOutDate(),
                ReservationStatus.CONFIRMED
        );

        if (!conflicts.isEmpty()) {
            String conflictedRooms = conflicts.stream()
                    .flatMap(r -> r.getRooms().stream())
                    .map(Room::getRoomNumber)
                    .distinct()
                    .collect(Collectors.joining(", "));
            throw new RuntimeException(
                    "Rooms already booked: " + conflictedRooms +
                            " between " + request.getCheckInDate() +
                            " and " + request.getCheckOutDate()
            );
        }

        // 💰 calcul total bazat pe planurile tarifare suprapuse
        BigDecimal total = calculateTotalAmount(request.getCheckInDate(), request.getCheckOutDate(), ratePlans);

        // ✅ determină planul tarifar activ la check-in (doar pentru referință)
        RatePlan activePlan = ratePlans.stream()
                .filter(plan -> !request.getCheckInDate().isBefore(plan.getStartDate()) &&
                        !request.getCheckInDate().isAfter(plan.getEndDate()))
                .findFirst()
                .orElse(ratePlans.get(0));

        // ✅ creare obiect rezervare
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setRatePlan(activePlan);
        reservation.setRooms(rooms);
        reservation.setCheckInDate(request.getCheckInDate());
        reservation.setCheckOutDate(request.getCheckOutDate());
        reservation.setTotalAmount(total);
        reservation.setStatus(ReservationStatus.ONHOLD);
        reservation.setCreatedAt(LocalDate.now());

        return toResponseDto(reservationRepository.save(reservation));
    }

    // 🔹 Confirmare rezervare existentă
    @Override
    public ReservationResponseDto confirmReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation.getRooms().forEach(room -> room.setStatus(RoomStatus.BOOKED));
        roomRepository.saveAll(reservation.getRooms());

        return toResponseDto(reservationRepository.save(reservation));
    }

    // 🔹 Anulare rezervare
    @Override
    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
    }

    // 🔹 Toate rezervările
    @Override
    public List<ReservationResponseDto> findAllReservations() {
        return reservationRepository.findAll()
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    // 🔹 Rezervările unui utilizator
    @Override
    public List<ReservationResponseDto> findReservationsByUser(Long userId) {
        return reservationRepository.findByUserId(userId)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    // 🔹 Căutare rezervare după ID
    @Override
    public ReservationResponseDto findById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        return toResponseDto(reservation);
    }

    // 🔹 Calcul total în funcție de rate plans
    private BigDecimal calculateTotalAmount(LocalDate checkIn, LocalDate checkOut, List<RatePlan> ratePlans) {
        return java.util.stream.Stream
                .iterate(checkIn, date -> date.isBefore(checkOut), date -> date.plusDays(1)) // 👈 generează fiecare zi din interval
                .map(date -> {
                    // găsește planul tarifar valabil pentru acea zi
                    return ratePlans.stream()
                            .filter(p -> !date.isBefore(p.getStartDate()) && !date.isAfter(p.getEndDate()))
                            .findFirst()
                            .map(RatePlan::getPricePerNight)
                            .orElseThrow(() -> new RuntimeException("No rate plan found for date: " + date));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add); // 👈 adună toate prețurile
    }


    // 🔹 Mapper entitate → DTO
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