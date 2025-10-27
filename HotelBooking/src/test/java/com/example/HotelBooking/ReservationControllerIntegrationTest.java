package com.example.HotelBooking;

import com.example.HotelBooking.model.dto.ReservationRequestDto;
import com.example.HotelBooking.model.entities.*;
import com.example.HotelBooking.model.enums.ReservationStatus;
import com.example.HotelBooking.repo.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")  // spune Spring să folosească application-test.properties
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class ReservationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RatePlanRepository ratePlanRepository;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    private Hotel hotel;
    private Room room;
    private User user;
    private RatePlan ratePlan;
    private RoomType type;

    @BeforeEach
    void setup() {
        // Curățăm tabelele
        reservationRepository.deleteAll();
        ratePlanRepository.deleteAll();
        roomRepository.deleteAll();
        userRepository.deleteAll();
        hotelRepository.deleteAll();
        roomTypeRepository.deleteAll();

        //  Hotel
        hotel = new Hotel();
        hotel.setName("Inter");
        hotel.setLocation("București");
        hotel.setRating(5.0);
        hotelRepository.save(hotel);

        //  User
        user = new User();
        user.setName("Test User");
        userRepository.save(user);

        //  RoomType
        type = new RoomType();
        type.setName("Double");
        type.setDescription("2 persoane");
        type.setCapacity(2);
        roomTypeRepository.save(type);

        //  Room
        room = new Room();
        room.setHotel(hotel);
        room.setRoomType(type);
        room.setRoomNumber("101");
        roomRepository.save(room);

        //  RatePlan
        ratePlan = new RatePlan();
        ratePlan.setHotel(hotel);
        ratePlan.setRoomType(type);
        ratePlan.setPricePerNight(BigDecimal.valueOf(100));
        ratePlan.setStartDate(LocalDate.of(2025, 10, 20));
        ratePlan.setEndDate(LocalDate.of(2025, 10, 30));
        ratePlanRepository.save(ratePlan);
    }

    //  Test principal — creare rezervare validă
    @Test
    void whenCreateValidReservation_thenReturnOkAndSaveToDatabase() throws Exception {
        ReservationRequestDto dto = new ReservationRequestDto();
        dto.setUserId(user.getId());
        dto.setRatePlanId(ratePlan.getId());
        dto.setRoomIds(Set.of(room.getId()));
        dto.setCheckInDate(LocalDate.of(2025, 10, 22));
        dto.setCheckOutDate(LocalDate.of(2025, 10, 24));

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ReservationStatus.ONHOLD.name()))
                .andExpect(jsonPath("$.totalAmount").value(200.00)); //  JSON tolerant la .00

        //  Verificare în baza de date
        Reservation saved = reservationRepository.findAll().get(0);
        assertEquals(ReservationStatus.ONHOLD, saved.getStatus());
        assertEquals(0, saved.getTotalAmount().compareTo(BigDecimal.valueOf(200))); //  tolerant la scale
        assertEquals(hotel.getId(), saved.getHotel().getId());
        assertEquals(user.getId(), saved.getUser().getId());
    }

    //  Test negativ — plan tarifar din alt hotel
    @Test
    void whenCreateReservationWithInvalidRatePlan_thenReturnError() throws Exception {
        // alt hotel
        Hotel otherHotel = new Hotel();
        otherHotel.setName("Hilton");
        otherHotel.setLocation("Cluj");
        otherHotel.setRating(5.0);
        hotelRepository.save(otherHotel);

        // plan tarifar pentru alt hotel
        RatePlan otherPlan = new RatePlan();
        otherPlan.setHotel(otherHotel);
        otherPlan.setRoomType(type);
        otherPlan.setPricePerNight(BigDecimal.valueOf(150));
        otherPlan.setStartDate(LocalDate.of(2025, 10, 20));
        otherPlan.setEndDate(LocalDate.of(2025, 10, 25));
        ratePlanRepository.save(otherPlan);

        ReservationRequestDto dto = new ReservationRequestDto();
        dto.setUserId(user.getId());
        dto.setRatePlanId(otherPlan.getId());
        dto.setRoomIds(Set.of(room.getId()));
        dto.setCheckInDate(LocalDate.of(2025, 10, 22));
        dto.setCheckOutDate(LocalDate.of(2025, 10, 24));

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.message")
                        .value(org.hamcrest.Matchers.containsString("Rate plan belongs to another hotel")));

        assertTrue(reservationRepository.findAll().isEmpty());
    }
}
