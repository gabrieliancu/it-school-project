package com.example.HotelBooking;
import com.example.HotelBooking.model.dto.ReservationRequestDto;
import com.example.HotelBooking.model.entities.*;
import com.example.HotelBooking.model.enums.ReservationStatus;
import com.example.HotelBooking.model.enums.RoomStatus;
import com.example.HotelBooking.repo.*;
import com.example.HotelBooking.service.implementation.ReservationServiceImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceImplementationTest {

    @InjectMocks
    private ReservationServiceImplementation reservationService;

    @Mock private ReservationRepository reservationRepository;
    @Mock private UserRepository userRepository;
    @Mock private RoomRepository roomRepository;
    @Mock private RatePlanRepository ratePlanRepository;

    private User user;
    private Room room;
    private RatePlan ratePlan;

    @BeforeEach
    void setUp() {
        user = new User(1L, "John Doe", "john@example.com", "0700000000");
        room = new Room(1L, RoomStatus.AVAILABLE, "101", "1", null, null);
        ratePlan = new RatePlan(1L, null, null, new BigDecimal("300.00"), "Flexible", null, null);
    }

    @Test
    void whenValidRequest_thenCreateReservationSuccessfully() {
        // arrange
        ReservationRequestDto dto = new ReservationRequestDto();
        dto.setUserId(1L);
        dto.setRatePlanId(1L);
        dto.setRoomIds(Set.of(1L));
        dto.setCheckInDate(LocalDate.of(2025, 11, 10));
        dto.setCheckOutDate(LocalDate.of(2025, 11, 12));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(ratePlanRepository.findById(1L)).thenReturn(Optional.of(ratePlan));
        when(roomRepository.findAllById(any())).thenReturn(List.of(room));
        when(reservationRepository.findConflictingReservations(any(), any(), any())).thenReturn(Collections.emptyList());
        when(reservationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // act
        var response = reservationService.createReservation(dto);

        // assert
        assertNotNull(response);
        assertEquals("ONHOLD", response.getStatus());
        assertEquals(new BigDecimal("600.00"), response.getTotalAmount());
        verify(reservationRepository, times(1)).save(any());
    }

    @Test
    void whenConflictingReservationExists_thenThrowException() {
        // arrange
        ReservationRequestDto dto = new ReservationRequestDto();
        dto.setUserId(1L);
        dto.setRatePlanId(1L);
        dto.setRoomIds(Set.of(1L));
        dto.setCheckInDate(LocalDate.of(2025, 11, 10));
        dto.setCheckOutDate(LocalDate.of(2025, 11, 12));

        Reservation conflict = new Reservation();
        conflict.setStatus(ReservationStatus.CONFIRMED);
        conflict.setRooms(Set.of(room));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(ratePlanRepository.findById(1L)).thenReturn(Optional.of(ratePlan));
        when(roomRepository.findAllById(any())).thenReturn(List.of(room));
        when(reservationRepository.findConflictingReservations(any(), any(), any()))
                .thenReturn(List.of(conflict));

        // act & assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> reservationService.createReservation(dto));
        assertTrue(ex.getMessage().contains("already booked"));
        verify(reservationRepository, never()).save(any());
    }
}

