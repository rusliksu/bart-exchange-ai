package com.example.bartexchangeai.service.impl;

import com.example.bartexchangeai.dto.GroupDto;
import com.example.bartexchangeai.exception.DuplicateResourceException;
import com.example.bartexchangeai.exception.ResourceNotFoundException;
import com.example.bartexchangeai.mapper.GroupMapper;
import com.example.bartexchangeai.model.group.Group;
import com.example.bartexchangeai.repository.GroupRepository;
import com.example.bartexchangeai.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final GroupMapper groupMapper;

    @Override
    public Page<GroupDto> findAll(Pageable pageable) {
        return groupRepository.findAll(pageable).map(groupMapper::toDto);
    }

    @Override
    public GroupDto findById(Long id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group", id));
        return groupMapper.toDto(group);
    }

    @Override
    public GroupDto findByName(String name) {
        Group group = groupRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Группа с name=" + name + " не найдена"));
        return groupMapper.toDto(group);
    }

    @Override
    public Page<GroupDto> findByMemberId(Long userId, Pageable pageable) {
        return groupRepository.findPageByMemberId(userId, pageable).map(groupMapper::toDto);
    }

    @Override
    public Page<GroupDto> search(String keyword, Pageable pageable) {
        return groupRepository.searchByKeyword(keyword, pageable).map(groupMapper::toDto);
    }

    @Override
    @Transactional
    public GroupDto create(GroupDto groupDto) {
        if (groupRepository.existsByName(groupDto.getName())) {
            throw new DuplicateResourceException("Группа с name=" + groupDto.getName() + " уже существует");
        }
        Group group = groupMapper.toEntity(groupDto);
        return groupMapper.toDto(groupRepository.save(group));
    }

    @Override
    @Transactional
    public GroupDto update(Long id, GroupDto groupDto) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group", id));
        group.setName(groupDto.getName());
        group.setDescription(groupDto.getDescription());
        return groupMapper.toDto(groupRepository.save(group));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!groupRepository.existsById(id)) {
            throw new ResourceNotFoundException("Group", id);
        }
        groupRepository.deleteById(id);
    }
}
