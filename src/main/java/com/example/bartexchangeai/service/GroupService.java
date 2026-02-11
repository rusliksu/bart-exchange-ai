package com.example.bartexchangeai.service;

import com.example.bartexchangeai.dto.GroupDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GroupService {

    Page<GroupDto> findAll(Pageable pageable);

    GroupDto findById(Long id);

    GroupDto findByName(String name);

    Page<GroupDto> findByMemberId(Long userId, Pageable pageable);

    Page<GroupDto> search(String keyword, Pageable pageable);

    GroupDto create(GroupDto groupDto);

    GroupDto update(Long id, GroupDto groupDto);

    void delete(Long id);
}
