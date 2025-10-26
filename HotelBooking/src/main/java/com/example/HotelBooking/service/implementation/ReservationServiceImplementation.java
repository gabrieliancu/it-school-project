package com.example.HotelBooking.service.implementation;

import com.example.HotelBooking.model.dto.ReservationRequestDto;
import com.example.HotelBooking.model.dto.ReservationResponseDto;
import com.example.HotelBooking.model.entities.*;
import com.example.HotelBooking.model.enums.ReservationStatus;
import com.example.HotelBooking.model.enums.RoomStatus;
import com.example.HotelBooking.repo.*;
import com.example.HotelBooking.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    // ✅ Creare rezervare nouă
    @Override
    public ReservationResponseDto createReservation(ReservationRequestDto request) {

        // ✅ verifică existența utilizatorului
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ verifică camerele selectate
        Set<Room> rooms = new HashSet<>(roomRepository.findAllById(request.getRoomIds()));
        if (rooms.isEmpty()) {
            throw new RuntimeException("No rooms selected");
        }

        Room sampleRoom = rooms.iterator().next();

        // ✅ verifică planul tarifar (ratePlanId)
        RatePlan selectedPlan = ratePlanRepository.findById(request.getRatePlanId())
                .orElseThrow(() -> new RuntimeException("Invalid rate plan ID: " + request.getRatePlanId()));

        // 🔒 Verifică dacă planul tarifar aparține aceluiași hotel și tip de cameră
        if (!selectedPlan.getHotel().getId().equals(sampleRoom.getHotel().getId())) {
            throw new RuntimeException("❌ Rate plan belongs to another hotel ("
                    + selectedPlan.getHotel().getName() + ")");
        }

        if (!selectedPlan.getRoomType().getId().equals(sampleRoom.getRoomType().getId())) {
            throw new RuntimeException("❌ Rate plan does not match the selected room type ("
                    + selectedPlan.getRoomType().getName() + ")");
        }

        // ✅ obține doar planurile active pentru perioada cerută
        List<RatePlan> ratePlans = ratePlanRepository.findActiveRatePlansByHotelAndRoomType(
                sampleRoom.getHotel().getId(),
                sampleRoom.getRoomType().getId(),
                request.getCheckInDate(),
                request.getCheckOutDate()
        );

        if (ratePlans.isEmpty()) {
            throw new RuntimeException("No active rate plans found for this room type in the selected period");
        }

        // ✅ verifică conflictele cu alte rezervări confirmate (NUMAI din același hotel)
        List<Reservation> conflicts = reservationRepository.findConflictingReservationsByHotel(
                new ArrayList<>(request.getRoomIds()),
                request.getCheckInDate(),
                request.getCheckOutDate(),
                ReservationStatus.CONFIRMED,
                sampleRoom.getHotel().getId()
        );

        if (!conflicts.isEmpty()) {
            String conflictedRooms = conflicts.stream()
                    .flatMap(r -> r.getRooms().stream())
                    .map(Room::getRoomNumber)
                    .distinct()
                    .collect(Collectors.joining(", "));

            throw new RuntimeException(
                    "Rooms already booked: " + conflictedRooms + " between "
                            + request.getCheckInDate() + " and " + request.getCheckOutDate()
            );
        }

        // 💰 Calcul total corect bazat pe planurile tarifare suprapuse
        BigDecimal total = calculateTotalAmount(request.getCheckInDate(), request.getCheckOutDate(), ratePlans);

        // ✅ creează rezervarea
        Reservation reservation = new Reservation();
        reservation.setHotel(sampleRoom.getHotel());
        reservation.setUser(user);
        reservation.setRatePlan(selectedPlan);
        reservation.setRooms(rooms);
        reservation.setCheckInDate(request.getCheckInDate());
        reservation.setCheckOutDate(request.getCheckOutDate());
        reservation.setTotalAmount(total);
        reservation.setStatus(ReservationStatus.ONHOLD);
        reservation.setCreatedAt(LocalDate.now());

        return toResponseDto(reservationRepository.save(reservation));
    }

    // ✅ Confirmare rezervare
    @Override
    public ReservationResponseDto confirmReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        List<Long> roomIds = reservation.getRooms().stream()
                .map(Room::getId)
                .toList();

        Long hotelId = reservation.getRatePlan().getHotel().getId();

        // ✅ verifică conflictele doar în cadrul aceluiași hotel
        List<Reservation> conflicts = reservationRepository.findConflictingReservationsByHotel(
                roomIds,
                reservation.getCheckInDate(),
                reservation.getCheckOutDate(),
                ReservationStatus.CONFIRMED,
                hotelId
        );

        // eliminăm chiar rezervarea curentă din lista conflictelor (dacă apare)
        conflicts = conflicts.stream()
                .filter(r -> !r.getId().equals(reservationId))
                .toList();

        if (!conflicts.isEmpty()) {
            String conflictedRooms = conflicts.stream()
                    .flatMap(r -> r.getRooms().stream())
                    .map(Room::getRoomNumber)
                    .distinct()
                    .collect(Collectors.joining(", "));

            // ❌ DOAR ARUNCĂ EROARE — fără să o marcheze CANCELLED
            throw new RuntimeException(
                    "❌ Cannot confirm reservation. Conflicts detected with other confirmed bookings for rooms: "
                            + conflictedRooms + " between "
                            + reservation.getCheckInDate() + " and " + reservation.getCheckOutDate()
            );
        }

        // ✅ dacă nu există conflicte → confirmă rezervarea
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation.getRooms().forEach(room -> room.setStatus(RoomStatus.BOOKED));
        roomRepository.saveAll(reservation.getRooms());

        return toResponseDto(reservationRepository.save(reservation));
    }

    // ✅ Anulare rezervare — eliberează camerele automat
    @Override
    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.getRooms().forEach(room -> room.setStatus(RoomStatus.AVAILABLE));

        roomRepository.saveAll(reservation.getRooms());
        reservationRepository.save(reservation);
    }

    // ✅ Toate rezervările
    @Override
    public List<ReservationResponseDto> findAllReservations() {
        return reservationRepository.findAll().stream()
                .map(this::toResponseDto)
                .toList();
    }

    // ✅ Rezervări după utilizator
    @Override
    public List<ReservationResponseDto> findReservationsByUser(Long userId) {
        return reservationRepository.findByUserId(userId).stream()
                .map(this::toResponseDto)
                .toList();
    }

    // ✅ Căutare după ID
    @Override
    public ReservationResponseDto findById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        return toResponseDto(reservation);
    }

    // 💰 Calcul total pentru fiecare zi din perioada de ședere
    private BigDecimal calculateTotalAmount(LocalDate checkIn, LocalDate checkOut, List<RatePlan> ratePlans) {
        BigDecimal total = BigDecimal.ZERO;

        ratePlans.sort(Comparator.comparing(RatePlan::getStartDate));

        for (LocalDate date = checkIn; date.isBefore(checkOut); date = date.plusDays(1)) {
            final LocalDate currentDate = date;

            RatePlan matchingPlan = ratePlans.stream()
                    .filter(p -> (p.getStartDate().isEqual(currentDate) || p.getStartDate().isBefore(currentDate))
                            && (p.getEndDate().isEqual(currentDate) || p.getEndDate().isAfter(currentDate)))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("⚠️ No rate plan found for date: " + currentDate));

            total = total.add(matchingPlan.getPricePerNight());
        }

        return total;
    }

    // 🔁 Conversie Reservation → DTO
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
