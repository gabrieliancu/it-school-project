package com.example.HotelBooking.model.dto;

import com.example.HotelBooking.model.enums.RoomStatus;
import lombok.Data;

@Data
public class RoomDto {
private RoomStatus status;
    private String roomNumber;
    private String floor;
    private Long hotelId;
}
