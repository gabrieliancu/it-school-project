package com.example.HotelBooking.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class ReservationResponseDto {
    private Long reservationId;
    private String status;
    private LocalDateTime createdAt;
    private BigDecimal totalAmount;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
}
