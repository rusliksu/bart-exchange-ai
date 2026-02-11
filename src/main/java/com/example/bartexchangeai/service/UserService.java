package com.example.bartexchangeai.service;

import com.example.bartexchangeai.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    Page<UserDto> findAll(Pageable pageable);

    UserDto findById(Long id);

    UserDto findByUsername(String username);

    Page<UserDto> findByMinRating(Float minRating, Pageable pageable);

    UserDto create(UserDto userDto);

    UserDto update(Long id, UserDto userDto);

    void delete(Long id);
}
