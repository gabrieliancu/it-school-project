package com.example.HotelBooking.model.dto;

import com.example.HotelBooking.model.entities.Room;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
public class ReservationDto {
    private Long id;
    private Long userId;
    private Long ratePlanId;
    private Set<Long> roomIds = new HashSet<>();
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime createdAt;
}
