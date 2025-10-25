package com.example.HotelBooking.service;

import com.example.HotelBooking.model.dto.RoomTypeDto;
import com.example.HotelBooking.model.entities.RoomType;

import java.util.List;
import java.util.Optional;

public interface RoomTypeService {
    List<RoomType> findAllRoomTypes();

    Optional<RoomType> findById(Long id);

    Optional<RoomType> findByName(String name);

    List<RoomType> findByCapacity(int minCapacity);

    RoomType createRoomType(RoomTypeDto dto);

    RoomType updateRoomType(Long id, RoomTypeDto dto);

    void deleteRoomType(Long id);

    default RoomType toEntity(RoomTypeDto dto) {
        RoomType rt = new RoomType();
        rt.setName(dto.getName());
        rt.setDescription(dto.getDescription());
        rt.setCapacity(dto.getCapacity());
        return rt;
    }
}
