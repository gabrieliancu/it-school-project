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

    // 🔹 Creare plan tarifar nou
    @Override
    public RatePlan createRatePlan(RatePlanDto dto) {
        Hotel hotel = hotelRepository.findById(dto.getHotelId())
                .orElseThrow(() ->
                        new EntityNotFoundException("❌ Hotel with ID " + dto.getHotelId() + " not found."));

        RoomType roomType = roomTypeRepository.findById(dto.getRoomTypeId())
                .orElseThrow(() ->
                        new EntityNotFoundException("❌ RoomType with ID " + dto.getRoomTypeId() + " not found."));

        // 🧠 validare logică de suprapunere
        List<RatePlan> overlappingPlans = ratePlanRepository.findActiveRatePlansByHotelAndRoomType(
                hotel.getId(),
                roomType.getId(),
                dto.getStartDate(),
                dto.getEndDate()
        );
        if (!overlappingPlans.isEmpty()) {
            throw new IllegalArgumentException("⚠️ There is already a rate plan overlapping with the given period!");
        }

        RatePlan ratePlan = new RatePlan();
        ratePlan.setHotel(hotel);
        ratePlan.setRoomType(roomType);
        ratePlan.setPricePerNight(dto.getPricePerNight());
        ratePlan.setCancellationPolicy(dto.getCancellationPolicy());
        ratePlan.setStartDate(dto.getStartDate());
        ratePlan.setEndDate(dto.getEndDate());

        return ratePlanRepository.save(ratePlan);
    }

    // 🔹 Returnează toate planurile
    @Override
    public List<RatePlan> findAllRatePlans() {
        return ratePlanRepository.findAll();
    }

    // 🔹 Căutare după ID
    @Override
    public Optional<RatePlan> findRatePlanById(Long id) {
        return ratePlanRepository.findById(id);
    }

    // 🔹 Căutare după hotel
    @Override
    public List<RatePlan> findRatePlansByHotel(Long hotelId) {
        return ratePlanRepository.findByHotelId(hotelId);
    }

    // 🔹 Căutare după tip cameră
    @Override
    public List<RatePlan> findRatePlansByRoomType(Long roomTypeId) {
        return ratePlanRepository.findByRoomTypeId(roomTypeId);
    }

    // 🔹 Planuri active între două date (toate hotelurile / tipurile)
    @Override
    public List<RatePlan> findActiveRatePlans(LocalDate start, LocalDate end) {
        return ratePlanRepository.findActiveRatePlans(start, end);
    }

    // 🔹 Planuri active pentru un hotel și tip de cameră
    public List<RatePlan> findActiveRatePlansByHotelAndRoomType(Long hotelId, Long roomTypeId,
                                                                LocalDate start, LocalDate end) {
        return ratePlanRepository.findActiveRatePlansByHotelAndRoomType(hotelId, roomTypeId, start, end);
    }

    // 🔹 Actualizare plan tarifar
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

        // (opțional) validare logică: startDate <= endDate
        if (ratePlan.getStartDate().isAfter(ratePlan.getEndDate())) {
            throw new IllegalArgumentException("❌ Start date cannot be after end date.");
        }

        return ratePlanRepository.save(ratePlan);
    }

    // 🔹 Ștergere plan tarifar
    @Override
    public void deleteRatePlan(Long id) {
        if (!ratePlanRepository.existsById(id)) {
            throw new EntityNotFoundException("❌ RatePlan with ID " + id + " not found.");
        }
        ratePlanRepository.deleteById(id);
    }
}