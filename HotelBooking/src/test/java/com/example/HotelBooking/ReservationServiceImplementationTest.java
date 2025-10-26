package com.example.HotelBooking;
import com.example.HotelBooking.model.dto.ReservationRequestDto;
import com.example.HotelBooking.model.dto.ReservationResponseDto;
import com.example.HotelBooking.model.entities.*;
import com.example.HotelBooking.model.enums.ReservationStatus;
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

    @Mock private ReservationRepository reservationRepository;
    @Mock private UserRepository userRepository;
    @Mock private RoomRepository roomRepository;
    @Mock private RatePlanRepository ratePlanRepository;

    @InjectMocks
    private ReservationServiceImplementation reservationService;

    private ReservationRequestDto request;
    private User user;
    private RatePlan ratePlan;
    private Room room;

    @BeforeEach
    void setup() {
        user = new User();
        user.setId(1L);

        ratePlan = new RatePlan();
        ratePlan.setId(1L);
        ratePlan.setPricePerNight(BigDecimal.valueOf(100));

        room = new Room();
        room.setId(1L);
        room.setRoomNumber("101");

        request = new ReservationRequestDto();
        request.setUserId(1L);
        request.setRatePlanId(1L);
        request.setRoomIds(Set.of(1L));
        request.setCheckInDate(LocalDate.now().plusDays(1));
        request.setCheckOutDate(LocalDate.now().plusDays(3));
    }

    @Test
    void whenValidReservation_thenCreateSuccessfully() {
        // mock dependencies
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(ratePlanRepository.findById(1L)).thenReturn(Optional.of(ratePlan));
        when(roomRepository.findAllById(anyList())).thenReturn(List.of(room));
        when(reservationRepository.findConflictingReservationsByHotel(anyList(), any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        Reservation saved = new Reservation();
        saved.setId(10L);
        saved.setUser(user);
        saved.setStatus(ReservationStatus.ONHOLD);
        saved.setTotalAmount(BigDecimal.valueOf(200));
        when(reservationRepository.save(any())).thenReturn(saved);

        // act
        ReservationResponseDto response = reservationService.createReservation(request);

        // assert
        assertNotNull(response);
        assertEquals("ONHOLD", response.getStatus());
        assertEquals(BigDecimal.valueOf(200), response.getTotalAmount());
        verify(reservationRepository, times(1)).save(any());
    }

    @Test
    void whenRoomsConflict_thenThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(ratePlanRepository.findById(1L)).thenReturn(Optional.of(ratePlan));
        when(roomRepository.findAllById(anyList())).thenReturn(List.of(room));

        when(reservationRepository.findConflictingReservationsByHotel(anyList(), any(), any(), any(), any()))
                .thenReturn(List.of(new Reservation())); // simulate conflict

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> reservationService.createReservation(request));

        assertTrue(ex.getMessage().contains("Rooms already booked"));
    }
}