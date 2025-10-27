package com.example.HotelBooking.repo;
import com.example.HotelBooking.model.entities.RatePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RatePlanRepository extends JpaRepository<RatePlan, Long> {

    //  Toate planurile unui hotel
    List<RatePlan> findByHotelId(Long hotelId);

    //  Toate planurile pentru un anumit tip de cameră
    List<RatePlan> findByRoomTypeId(Long roomTypeId);

    //  Planuri care se suprapun cu perioada cerută (suprapuneri parțiale sau totale)
    @Query("""
        SELECT r FROM RatePlan r
        WHERE r.endDate >= :start
          AND r.startDate <= :end
    """)
    List<RatePlan> findActiveRatePlans(@Param("start") LocalDate start,
                                       @Param("end") LocalDate end);

    //  Planuri active pentru un hotel și un tip de cameră specific
    @Query("""
        SELECT r FROM RatePlan r
        WHERE r.hotel.id = :hotelId
          AND r.roomType.id = :roomTypeId
          AND r.endDate >= :start
          AND r.startDate <= :end
        ORDER BY r.startDate
    """)
    List<RatePlan> findActiveRatePlansByHotelAndRoomType(
            @Param("hotelId") Long hotelId,
            @Param("roomTypeId") Long roomTypeId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    //  Toate planurile pentru un hotel și un tip de cameră (fără filtrare pe perioadă)
    @Query("""
        SELECT r FROM RatePlan r
        WHERE r.hotel.id = :hotelId
          AND r.roomType.id = :roomTypeId
        ORDER BY r.startDate
    """)
    List<RatePlan> findAllByHotelIdAndRoomTypeId(
            @Param("hotelId") Long hotelId,
            @Param("roomTypeId") Long roomTypeId
    );
}