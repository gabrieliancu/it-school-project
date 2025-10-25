package com.example.HotelBooking;

import com.example.HotelBooking.model.dto.ReservationRequestDto;
import com.example.HotelBooking.model.entities.*;
import com.example.HotelBooking.model.enums.RoomStatus;
import com.example.HotelBooking.repo.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class ReservationControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private HotelRepository hotelRepository;
    @Autowired private RoomRepository roomRepository;
    @Autowired private RatePlanRepository ratePlanRepository;
    @Autowired private ReservationRepository reservationRepository;

    private User user;
    private Hotel hotel;
    private Room room;
    private RatePlan ratePlan;

    @BeforeEach
    void setUp() {
        reservationRepository.deleteAll();
        ratePlanRepository.deleteAll();
        roomRepository.deleteAll();
        hotelRepository.deleteAll();
        userRepository.deleteAll();

        user = userRepository.save(new User(null, "John Doe", "john@example.com", "0700000000"));
        hotel = hotelRepository.save(new Hotel(null, "Hilton", "Bucharest", 4.8));
        room = roomRepository.save(new Room(null, RoomStatus.AVAILABLE, "101", "1", hotel, null));
        ratePlan = ratePlanRepository.save(
                new RatePlan(null, hotel, null, new BigDecimal("300.00"), "Flexible", null, null)
        );
    }

    @Test
    void whenCreateValidReservation_thenReturnOkAndSaveInDatabase() throws Exception {
        // Given
        ReservationRequestDto request = new ReservationRequestDto();
        request.setUserId(user.getId());
        request.setRatePlanId(ratePlan.getId());
        request.setRoomIds(Set.of(room.getId()));
        request.setCheckInDate(LocalDate.of(2025, 11, 10));
        request.setCheckOutDate(LocalDate.of(2025, 11, 12));

        // When
        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                // Then - verificăm răspunsul
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ONHOLD"))
                .andExpect(jsonPath("$.totalAmount").value(600.00));

        // Verificăm în baza de date
        List<Reservation> saved = reservationRepository.findAll();
        assertThat(saved).hasSize(1);
        assertThat(saved.get(0).getTotalAmount()).isEqualByComparingTo(new BigDecimal("600.00"));
    }

    @Test
    void whenInvalidUser_thenReturnServerError() throws Exception {
        // Given
        ReservationRequestDto request = new ReservationRequestDto();
        request.setUserId(999L); // utilizator inexistent
        request.setRatePlanId(ratePlan.getId());
        request.setRoomIds(Set.of(room.getId()));
        request.setCheckInDate(LocalDate.of(2025, 11, 10));
        request.setCheckOutDate(LocalDate.of(2025, 11, 12));

        // When & Then
        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is5xxServerError());
    }
}
