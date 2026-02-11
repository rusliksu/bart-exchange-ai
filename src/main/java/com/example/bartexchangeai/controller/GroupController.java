package com.example.bartexchangeai.controller;

import com.example.bartexchangeai.dto.GroupDto;
import com.example.bartexchangeai.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
@Validated
@Tag(name = "Groups", description = "Group management API")
public class GroupController {

    private final GroupService groupService;

    @GetMapping
    @Operation(summary = "Get all groups")
    public Page<GroupDto> getAllGroups(Pageable pageable) {
        return groupService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get group by ID")
    public GroupDto getGroupById(@PathVariable Long id) {
        return groupService.findById(id);
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get group by name")
    public GroupDto getGroupByName(@PathVariable String name) {
        return groupService.findByName(name);
    }

    @GetMapping("/member/{userId}")
    @Operation(summary = "Get groups by member user ID")
    public Page<GroupDto> getGroupsByMember(@PathVariable Long userId, Pageable pageable) {
        return groupService.findByMemberId(userId, pageable);
    }

    @GetMapping("/search")
    @Operation(summary = "Search groups by keyword")
    public Page<GroupDto> searchGroups(@RequestParam @Size(min = 1, max = 100) String keyword, Pageable pageable) {
        return groupService.search(keyword, pageable);
    }

    @PostMapping
    @Operation(summary = "Create a new group")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GroupDto> createGroup(@Valid @RequestBody GroupDto groupDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(groupService.create(groupDto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a group")
    @PreAuthorize("hasRole('ADMIN')")
    public GroupDto updateGroup(@PathVariable Long id, @Valid @RequestBody GroupDto groupDto) {
        return groupService.update(id, groupDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a group")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long id) {
        groupService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
