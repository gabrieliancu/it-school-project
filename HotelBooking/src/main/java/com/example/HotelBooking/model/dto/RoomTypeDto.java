package com.example.HotelBooking.model.dto;

import lombok.Data;

@Data
public class RoomTypeDto {
    private String name;
    private String description;
    private int capacity;
}
