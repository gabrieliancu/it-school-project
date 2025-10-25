package com.example.HotelBooking.repo;

import com.example.HotelBooking.model.entities.Reservation;
import com.example.HotelBooking.model.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUserId(Long userId);

    @Query("""
        select distinct r from Reservation r
        join r.rooms room
        where room.id in :roomIds
          and r.status = com.example.HotelBooking.model.enums.ReservationStatus.CONFIRMED
          and (
              (r.checkInDate <= :checkOut and r.checkOutDate >= :checkIn)
          )
    """)
    List<Reservation> findConflictingReservations(
            @Param("roomIds") Collection<Long> roomIds,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut);
}
