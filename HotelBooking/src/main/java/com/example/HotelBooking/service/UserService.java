package com.example.HotelBooking.service;

import com.example.HotelBooking.model.dto.UserDto;
import com.example.HotelBooking.model.entities.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(UserDto dto);

    List<User> findAllUsers();

    Optional<User> findUserById(Long id);

    Optional<User> findUserByEmail(String email);

    User updateUser(Long id, UserDto dto);

    void deleteUser(Long id);
}
