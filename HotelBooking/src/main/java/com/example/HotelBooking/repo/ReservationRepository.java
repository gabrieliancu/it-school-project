package com.example.HotelBooking.repo;

import com.example.HotelBooking.model.entities.Reservation;
import com.example.HotelBooking.model.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import static com.example.HotelBooking.model.enums.ReservationStatus.CONFIRMED;
import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUserId(Long userId);

    // verifică conflicte (camere deja rezervate în interval)
    @Query("""
    SELECT DISTINCT r FROM Reservation r
    JOIN r.rooms room
    WHERE room.id IN :roomIds
      AND r.status = :status
      AND r.ratePlan.hotel.id = :hotelId
      AND (r.checkInDate <= :checkOut AND r.checkOutDate >= :checkIn)
""")
    List<Reservation> findConflictingReservationsByHotel(
            @Param("roomIds") List<Long> roomIds,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut,
            @Param("status") ReservationStatus status,
            @Param("hotelId") Long hotelId
    );
}
