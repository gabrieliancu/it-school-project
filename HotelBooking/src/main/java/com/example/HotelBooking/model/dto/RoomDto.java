package com.example.HotelBooking.model.dto;

import lombok.Data;

@Data
public class RoomDto {
private String status;
    private String roomNumber;
    private String floor;
    private Long roomTypeId;
    private Long hotelId;
}
