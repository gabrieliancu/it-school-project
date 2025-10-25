package com.example.HotelBooking.model.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.Set;
@Data
public class ReservationRequestDto {
    private Long ratePlanId;
    private Long userId;
    private Set<Long> roomIds;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
}
