package com.example.bartexchangeai.service.impl;

import com.example.bartexchangeai.dto.UserDto;
import com.example.bartexchangeai.exception.DuplicateResourceException;
import com.example.bartexchangeai.exception.ResourceNotFoundException;
import com.example.bartexchangeai.mapper.UserMapper;
import com.example.bartexchangeai.model.user.User;
import com.example.bartexchangeai.repository.UserRepository;
import com.example.bartexchangeai.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public Page<UserDto> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toDto);
    }

    @Override
    public UserDto findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        return userMapper.toDto(user);
    }

    @Override
    public UserDto findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь с username=" + username + " не найден"));
        return userMapper.toDto(user);
    }

    @Override
    public Page<UserDto> findByMinRating(Float minRating, Pageable pageable) {
        return userRepository.findByMinRating(minRating, pageable).map(userMapper::toDto);
    }

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new DuplicateResourceException("Пользователь с username=" + userDto.getUsername() + " уже существует");
        }
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new DuplicateResourceException("Пользователь с email=" + userDto.getEmail() + " уже существует");
        }
        User user = userMapper.toEntity(userDto);
        User saved = userRepository.save(user);
        log.info("User created: id={}, username={}", saved.getId(), saved.getUsername());
        return userMapper.toDto(saved);
    }

    @Override
    @Transactional
    public UserDto update(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));

        if (!user.getUsername().equals(userDto.getUsername())
                && userRepository.existsByUsername(userDto.getUsername())) {
            throw new DuplicateResourceException("Username уже занят: " + userDto.getUsername());
        }
        if (!user.getEmail().equals(userDto.getEmail())
                && userRepository.existsByEmail(userDto.getEmail())) {
            throw new DuplicateResourceException("Email уже занят: " + userDto.getEmail());
        }

        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        // rating обновляется только через отзывы, не через API
        log.info("User updated: id={}", id);
        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", id);
        }
        userRepository.deleteById(id);
        log.warn("User deleted: id={}", id);
    }
}
