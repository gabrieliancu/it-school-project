package com.example.HotelBooking;
import com.example.HotelBooking.model.dto.ReservationRequestDto;
import com.example.HotelBooking.model.dto.ReservationResponseDto;
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

    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoomRepository roomRepository;
    @Mock
    private RatePlanRepository ratePlanRepository;

    private User user;
    private Hotel hotel;
    private Room room;
    private RatePlan ratePlan;
    private ReservationRequestDto request;

    @BeforeEach
    void setup() {
        hotel = new Hotel();
        hotel.setId(1L);
        hotel.setName("Inter");

        user = new User();
        user.setId(1L);

        RoomType roomType = new RoomType();
        roomType.setId(1L);
        roomType.setName("Double");

        room = new Room();
        room.setId(1L);
        room.setHotel(hotel);
        room.setRoomType(roomType); // ✅ Adaugă această linie!
        room.setStatus(RoomStatus.AVAILABLE);

        ratePlan = new RatePlan();
        ratePlan.setId(1L);
        ratePlan.setHotel(hotel);
        ratePlan.setRoomType(roomType); // ✅ Și aici!
        ratePlan.setStartDate(LocalDate.of(2025, 10, 20));
        ratePlan.setEndDate(LocalDate.of(2025, 10, 30));
        ratePlan.setPricePerNight(BigDecimal.valueOf(100));

        request = new ReservationRequestDto();
        request.setUserId(1L);
        request.setRatePlanId(1L);
        request.setRoomIds(Set.of(1L));
        request.setCheckInDate(LocalDate.of(2025, 10, 21));
        request.setCheckOutDate(LocalDate.of(2025, 10, 23));
    }

    @Test
    void whenValidRequest_thenCreateReservationSuccessfully() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roomRepository.findAllById(Set.of(1L))).thenReturn(List.of(room));
        when(ratePlanRepository.findById(1L)).thenReturn(Optional.of(ratePlan));
        when(ratePlanRepository.findActiveRatePlansByHotelAndRoomType(anyLong(), anyLong(), any(), any()))
                .thenReturn(new ArrayList<>(List.of(ratePlan)));
        when(reservationRepository.findConflictingReservationsByHotel(anyList(), any(), any(), any(), anyLong()))
                .thenReturn(Collections.emptyList());
        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(invocation -> {
                    Reservation r = invocation.getArgument(0);
                    r.setId(10L);
                    return r;
                });

        // when
        ReservationResponseDto result = reservationService.createReservation(request);

        // then
        assertNotNull(result);
        assertEquals(ReservationStatus.ONHOLD.name(), result.getStatus());
        assertEquals(BigDecimal.valueOf(200), result.getTotalAmount()); // 2 nopți * 100
        verify(reservationRepository).save(any(Reservation.class));
    }

    @Test
    void whenRatePlanBelongsToAnotherHotel_thenThrowException() {
        // given
        Hotel otherHotel = new Hotel();
        otherHotel.setId(2L);
        ratePlan.setHotel(otherHotel);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roomRepository.findAllById(Set.of(1L))).thenReturn(List.of(room));
        when(ratePlanRepository.findById(1L)).thenReturn(Optional.of(ratePlan));

        // when
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> reservationService.createReservation(request));

        // then
        assertTrue(ex.getMessage().contains("Rate plan belongs to another hotel"));
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void whenRoomsAlreadyBooked_thenThrowException() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roomRepository.findAllById(Set.of(1L))).thenReturn(List.of(room));
        when(ratePlanRepository.findById(1L)).thenReturn(Optional.of(ratePlan));
        when(ratePlanRepository.findActiveRatePlansByHotelAndRoomType(anyLong(), anyLong(), any(), any()))
                .thenReturn(List.of(ratePlan));

        // simulăm o rezervare conflictuală existentă
        Reservation conflict = new Reservation();
        conflict.setId(99L);
        when(reservationRepository.findConflictingReservationsByHotel(anyList(), any(), any(), any(), anyLong()))
                .thenReturn(List.of(conflict));

        // when
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> reservationService.createReservation(request));

        // then
        assertNotNull(ex.getMessage(), "Exception message should not be null");
        assertTrue(
                ex.getMessage().contains("Rooms already booked"),
                "Expected error message to contain 'Rooms already booked', but got: " + ex.getMessage()
        );

        // Verifică faptul că NU s-a salvat nicio rezervare
        verify(reservationRepository, never()).save(any());
    }


    @Test
    void whenConfirmReservationWithoutConflict_thenStatusConfirmed() {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setRooms(Set.of(room));
        reservation.setRatePlan(ratePlan);
        reservation.setCheckInDate(request.getCheckInDate());
        reservation.setCheckOutDate(request.getCheckOutDate());
        reservation.setStatus(ReservationStatus.ONHOLD);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.findConflictingReservationsByHotel(anyList(), any(), any(), any(), anyLong()))
                .thenReturn(Collections.emptyList());
        when(reservationRepository.save(any())).thenReturn(reservation);

        ReservationResponseDto result = reservationService.confirmReservation(1L);

        assertEquals(ReservationStatus.CONFIRMED.name(), result.getStatus());
        verify(reservationRepository, times(1)).save(any());
    }

    @Test
    void whenCancelReservation_thenRoomsBecomeAvailable() {
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setRooms(Set.of(room));
        reservation.setStatus(ReservationStatus.CONFIRMED);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        reservationService.cancelReservation(1L);

        assertEquals(ReservationStatus.CANCELLED, reservation.getStatus());
        assertEquals(RoomStatus.AVAILABLE, room.getStatus());
        verify(roomRepository).saveAll(anyCollection());
        verify(reservationRepository).save(reservation);
    }
}