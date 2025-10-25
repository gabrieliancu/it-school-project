package com.example.HotelBooking.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Data;

@Data
public class RatePlanDto {
    private Long hotelId;
    private Long roomTypeId;
    private BigDecimal pricePerNight;
    private String cancellationPolicy;
    private LocalDate startDate;
    private LocalDate endDate;
}
