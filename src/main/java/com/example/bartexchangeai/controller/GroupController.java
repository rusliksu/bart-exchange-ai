package com.example.bartexchangeai.controller;

import com.example.bartexchangeai.dto.GroupDto;
import com.example.bartexchangeai.mapper.GroupMapper;
import com.example.bartexchangeai.model.group.Group;
import com.example.bartexchangeai.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {
    
    private final GroupRepository groupRepository;
    private final GroupMapper groupMapper;
    
    @GetMapping
    public List<GroupDto> getAllGroups() {
        List<Group> groups = groupRepository.findAll();
        return groupMapper.toDtoList(groups);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<GroupDto> getGroupById(@PathVariable Long id) {
        Optional<Group> group = groupRepository.findById(id);
        return group.map(grp -> ResponseEntity.ok(groupMapper.toDto(grp)))
                   .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/name/{name}")
    public ResponseEntity<GroupDto> getGroupByName(@PathVariable String name) {
        Optional<Group> group = groupRepository.findByName(name);
        return group.map(grp -> ResponseEntity.ok(groupMapper.toDto(grp)))
                   .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/member/{userId}")
    public List<GroupDto> getGroupsByMember(@PathVariable Long userId) {
        List<Group> groups = groupRepository.findByMemberId(userId);
        return groupMapper.toDtoList(groups);
    }
    
    @GetMapping("/search")
    public List<GroupDto> searchGroups(@RequestParam String keyword) {
        List<Group> groups = groupRepository.findByNameOrDescriptionContaining(keyword);
        return groupMapper.toDtoList(groups);
    }
    
    @PostMapping
    public ResponseEntity<GroupDto> createGroup(@Valid @RequestBody GroupDto groupDto) {
        if (groupRepository.existsByName(groupDto.getName())) {
            return ResponseEntity.badRequest().build();
        }
        Group group = groupMapper.toEntity(groupDto);
        Group savedGroup = groupRepository.save(group);
        return ResponseEntity.ok(groupMapper.toDto(savedGroup));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<GroupDto> updateGroup(@PathVariable Long id, @Valid @RequestBody GroupDto groupDetails) {
        Optional<Group> optionalGroup = groupRepository.findById(id);
        if (optionalGroup.isPresent()) {
            Group group = optionalGroup.get();
            group.setName(groupDetails.getName());
            group.setDescription(groupDetails.getDescription());
            Group updatedGroup = groupRepository.save(group);
            return ResponseEntity.ok(groupMapper.toDto(updatedGroup));
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long id) {
        if (groupRepository.existsById(id)) {
            groupRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}