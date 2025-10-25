package com.example.HotelBooking.repo;

import com.example.HotelBooking.model.entities.RatePlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RatePlanRepository extends JpaRepository<RatePlan, Long> {

    // toate planurile unui hotel
    List<RatePlan> findByHotelId(Long hotelId);

    // planuri pentru un anumit tip de cameră
    List<RatePlan> findByRoomTypeId(Long roomTypeId);

    // planuri active într-o perioadă dată
    List<RatePlan> findByStartDateBeforeAndEndDateAfter(LocalDate start, LocalDate end);

    // planuri active pentru un hotel
    List<RatePlan> findByHotelIdAndStartDateBeforeAndEndDateAfter(Long hotelId, LocalDate start, LocalDate end);

}
