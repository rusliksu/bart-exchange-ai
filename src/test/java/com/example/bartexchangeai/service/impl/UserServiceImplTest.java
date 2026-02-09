package com.example.bartexchangeai.service.impl;

import com.example.bartexchangeai.dto.UserDto;
import com.example.bartexchangeai.exception.DuplicateResourceException;
import com.example.bartexchangeai.exception.ResourceNotFoundException;
import com.example.bartexchangeai.mapper.UserMapper;
import com.example.bartexchangeai.model.user.User;
import com.example.bartexchangeai.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void findAll_returnsPage() {
        Pageable pageable = PageRequest.of(0, 10);
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setRating(4.5f);

        UserDto userDto = new UserDto(1L, "testuser", "test@example.com", 4.5f);

        Page<User> userPage = new PageImpl<>(List.of(user));
        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(userMapper.toDto(user)).thenReturn(userDto);

        Page<UserDto> result = userService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("testuser", result.getContent().get(0).getUsername());
        verify(userRepository).findAll(pageable);
    }

    @Test
    void findById_found() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setRating(4.5f);

        UserDto userDto = new UserDto(1L, "testuser", "test@example.com", 4.5f);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
        verify(userRepository).findById(1L);
        verify(userMapper).toDto(user);
    }

    @Test
    void findById_notFound_throwsResourceNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.findById(99L));
        verify(userRepository).findById(99L);
    }

    @Test
    void findByUsername_found() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        UserDto userDto = new UserDto(1L, "testuser", "test@example.com", null);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.findByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void findByUsername_notFound_throwsResourceNotFoundException() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.findByUsername("unknown"));
        verify(userRepository).findByUsername("unknown");
    }

    @Test
    void create_success() {
        UserDto inputDto = new UserDto(null, "newuser", "new@example.com", 0.0f);
        User user = new User();
        user.setId(1L);
        user.setUsername("newuser");
        user.setEmail("new@example.com");
        user.setRating(0.0f);

        UserDto outputDto = new UserDto(1L, "newuser", "new@example.com", 0.0f);

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userMapper.toEntity(inputDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(outputDto);

        UserDto result = userService.create(inputDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("newuser", result.getUsername());
        verify(userRepository).existsByUsername("newuser");
        verify(userRepository).existsByEmail("new@example.com");
        verify(userRepository).save(user);
    }

    @Test
    void create_duplicateUsername_throwsDuplicateResourceException() {
        UserDto inputDto = new UserDto(null, "existing", "new@example.com", 0.0f);

        when(userRepository.existsByUsername("existing")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> userService.create(inputDto));
        verify(userRepository).existsByUsername("existing");
        verify(userRepository, never()).save(any());
    }

    @Test
    void create_duplicateEmail_throwsDuplicateResourceException() {
        UserDto inputDto = new UserDto(null, "newuser", "existing@example.com", 0.0f);

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> userService.create(inputDto));
        verify(userRepository).existsByEmail("existing@example.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    void update_success() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("oldname");
        existingUser.setEmail("old@example.com");
        existingUser.setRating(3.0f);

        UserDto updateDto = new UserDto(1L, "newname", "new@example.com", 4.5f);
        UserDto outputDto = new UserDto(1L, "newname", "new@example.com", 4.5f);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);
        when(userMapper.toDto(existingUser)).thenReturn(outputDto);

        UserDto result = userService.update(1L, updateDto);

        assertNotNull(result);
        assertEquals("newname", result.getUsername());
        assertEquals("new@example.com", result.getEmail());
        assertEquals(4.5f, result.getRating());
        verify(userRepository).findById(1L);
        verify(userRepository).save(existingUser);
    }

    @Test
    void delete_success() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.delete(1L);

        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void delete_notFound_throwsResourceNotFoundException() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> userService.delete(99L));
        verify(userRepository).existsById(99L);
        verify(userRepository, never()).deleteById(any());
    }
}
