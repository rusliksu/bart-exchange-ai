package com.example.bartexchangeai.service;

import com.example.bartexchangeai.dto.GroupDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GroupService {

    Page<GroupDto> findAll(Pageable pageable);

    GroupDto findById(Long id);

    GroupDto findByName(String name);

    List<GroupDto> findByMemberId(Long userId);

    List<GroupDto> search(String keyword);

    GroupDto create(GroupDto groupDto);

    GroupDto update(Long id, GroupDto groupDto);

    void delete(Long id);
}
