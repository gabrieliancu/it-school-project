package com.example.HotelBooking.service.implementation;

import com.example.HotelBooking.model.dto.RoomTypeDto;
import com.example.HotelBooking.model.entities.RoomType;
import com.example.HotelBooking.repo.RoomTypeRepository;
import com.example.HotelBooking.service.RoomTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoomTypeServiceImplementation implements RoomTypeService {

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @Override
    public List<RoomType> findAllRoomTypes() {
        return roomTypeRepository.findAll();
    }

    @Override
    public Optional<RoomType> findById(Long id) {
        return roomTypeRepository.findById(id);
    }

    @Override
    public Optional<RoomType> findByName(String name) {
        return roomTypeRepository.findByName(name);
    }

    @Override
    public List<RoomType> findByCapacity(int minCapacity) {
        return roomTypeRepository.findByCapacityGreaterThanEqual(minCapacity);
    }

    @Override
    public RoomType createRoomType(RoomTypeDto dto) {
        return roomTypeRepository.save(toEntity(dto));
    }

    @Override
    public RoomType updateRoomType(Long id, RoomTypeDto dto) {
        RoomType existing = roomTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room type not found: "+id));

        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setCapacity(dto.getCapacity());

        return roomTypeRepository.save(existing);
    }

    @Override
    public void deleteRoomType(Long id) {
        if (!roomTypeRepository.existsById(id)) {
            throw new RuntimeException("Room type not found");
        }
        roomTypeRepository.deleteById(id);
    }
}
