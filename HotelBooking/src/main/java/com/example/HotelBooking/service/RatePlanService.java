package com.example.HotelBooking.service;

import com.example.HotelBooking.model.dto.RatePlanDto;
import com.example.HotelBooking.model.entities.RatePlan;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RatePlanService {
    RatePlan createRatePlan(RatePlanDto dto);

    List<RatePlan> findAllRatePlans();

    Optional<RatePlan> findRatePlanById(Long id);

    List<RatePlan> findRatePlansByHotel(Long hotelId);

    List<RatePlan> findRatePlansByRoomType(Long roomTypeId);

    List<RatePlan> findActiveRatePlans(LocalDate start, LocalDate end);

    RatePlan updateRatePlan(Long id, RatePlanDto dto);

    void deleteRatePlan(Long id);
}
