package com.example.HotelBooking.service;

import com.example.HotelBooking.model.dto.ReservationDto;
import com.example.HotelBooking.model.dto.ReservationRequestDto;
import com.example.HotelBooking.model.dto.ReservationResponseDto;
import com.example.HotelBooking.model.entities.Reservation;
import com.example.HotelBooking.model.enums.ReservationStatus;
import java.util.List;

public interface ReservationService {

    ReservationResponseDto createReservation(ReservationRequestDto request);

    ReservationResponseDto confirmReservation(Long reservationId);

    void cancelReservation(Long reservationId);

    List<ReservationResponseDto> findAllReservations();

    List<ReservationResponseDto> findReservationsByUser(Long userId);

    ReservationResponseDto findById(Long id);
}

