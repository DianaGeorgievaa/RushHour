package com.primeholding.rushhours.controller;

import com.primeholding.rushhours.dto.ActivityDto;
import com.primeholding.rushhours.exception.BadRequestException;
import com.primeholding.rushhours.exception.ResourceNotFoundException;
import com.primeholding.rushhours.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {
    private ActivityService activityService;

    @Autowired
    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    public ResponseEntity<ActivityDto> get(@RequestParam int id) throws ResourceNotFoundException {
        return new ResponseEntity<>(activityService.get(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ActivityDto>> get() {
        return new ResponseEntity<>(activityService.get(), HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    public ResponseEntity<ActivityDto> create(@RequestBody ActivityDto activityDto) throws BadRequestException {
        return new ResponseEntity<>(activityService.create(activityDto), HttpStatus.CREATED);
    }

    @PatchMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    public ResponseEntity<ActivityDto> partialUpdate(@RequestParam int id, @RequestBody Map<String, String> updates) throws BadRequestException, ResourceNotFoundException {
        return new ResponseEntity<>(activityService.partialUpdate(id, updates), HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    public ResponseEntity<Void> delete(@RequestParam int id) throws ResourceNotFoundException {
        activityService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}