package com.example.HotelBooking.service.implementation;

import com.example.HotelBooking.model.dto.RatePlanDto;
import com.example.HotelBooking.model.entities.Hotel;
import com.example.HotelBooking.model.entities.RatePlan;
import com.example.HotelBooking.model.entities.RoomType;
import com.example.HotelBooking.repo.HotelRepository;
import com.example.HotelBooking.repo.RatePlanRepository;
import com.example.HotelBooking.repo.RoomTypeRepository;
import com.example.HotelBooking.service.RatePlanService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class RatePlanServiceImplementation implements RatePlanService {

    @Autowired
    private RatePlanRepository ratePlanRepository;
    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @Override
    public RatePlan createRatePlan(RatePlanDto dto) {
        // 🔹 validări mai clare
        Hotel hotel = hotelRepository.findById(dto.getHotelId())
                .orElseThrow(() ->
                        new EntityNotFoundException("❌ Hotel with ID " + dto.getHotelId() + " not found."));

        RoomType roomType = roomTypeRepository.findById(dto.getRoomTypeId())
                .orElseThrow(() ->
                        new EntityNotFoundException("❌ RoomType with ID " + dto.getRoomTypeId() + " not found."));

        RatePlan ratePlan = new RatePlan();
        ratePlan.setHotel(hotel);
        ratePlan.setRoomType(roomType);
        ratePlan.setPricePerNight(dto.getPricePerNight());
        ratePlan.setCancellationPolicy(dto.getCancellationPolicy());
        ratePlan.setStartDate(dto.getStartDate());
        ratePlan.setEndDate(dto.getEndDate());

        return ratePlanRepository.save(ratePlan);
    }

    @Override
    public List<RatePlan> findAllRatePlans() {
        return ratePlanRepository.findAll();
    }

    @Override
    public Optional<RatePlan> findRatePlanById(Long id) {
        return ratePlanRepository.findById(id);
    }

    @Override
    public List<RatePlan> findRatePlansByHotel(Long hotelId) {
        return ratePlanRepository.findByHotelId(hotelId);
    }

    @Override
    public List<RatePlan> findRatePlansByRoomType(Long roomTypeId) {
        return ratePlanRepository.findByRoomTypeId(roomTypeId);
    }

    @Override
    public List<RatePlan> findActiveRatePlans(LocalDate start, LocalDate end) {
        return ratePlanRepository.findByStartDateBeforeAndEndDateAfter(start, end);
    }

    @Override
    public RatePlan updateRatePlan(Long id, RatePlanDto dto) {
        RatePlan ratePlan = ratePlanRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("❌ RatePlan with ID " + id + " not found."));

        if (dto.getPricePerNight() != null)
            ratePlan.setPricePerNight(dto.getPricePerNight());
        if (dto.getCancellationPolicy() != null)
            ratePlan.setCancellationPolicy(dto.getCancellationPolicy());
        if (dto.getStartDate() != null)
            ratePlan.setStartDate(dto.getStartDate());
        if (dto.getEndDate() != null)
            ratePlan.setEndDate(dto.getEndDate());

        return ratePlanRepository.save(ratePlan);
    }

    @Override
    public void deleteRatePlan(Long id) {
        if (!ratePlanRepository.existsById(id)) {
            throw new EntityNotFoundException("❌ RatePlan with ID " + id + " not found.");
        }
        ratePlanRepository.deleteById(id);
    }
}
