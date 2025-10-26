package com.example.HotelBooking.repo;

import com.example.HotelBooking.model.entities.RatePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface RatePlanRepository extends JpaRepository<RatePlan, Long> {

    // toate planurile unui hotel
    List<RatePlan> findByHotelId(Long hotelId);

    // planuri pentru un anumit tip de cameră
    List<RatePlan> findByRoomTypeId(Long roomTypeId);

    // planuri active într-o perioadă dată
    List<RatePlan> findByStartDateBeforeAndEndDateAfter(LocalDate start, LocalDate end);

    @Query("""
    SELECT r FROM RatePlan r
    WHERE r.hotel.id = :hotelId
      AND r.roomType.id = :roomTypeId
    ORDER BY r.startDate
""")
    List<RatePlan> findAllByHotelIdAndRoomTypeId(Long hotelId, Long roomTypeId);
}
