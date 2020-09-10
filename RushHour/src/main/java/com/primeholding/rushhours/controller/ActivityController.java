package com.primeholding.rushhours.controller;

import com.primeholding.rushhours.constants.RushHoursAppConstants;
import com.primeholding.rushhours.dto.ActivityDto;
import com.primeholding.rushhours.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(RushHoursAppConstants.API_PATH + RushHoursAppConstants.ACTIVITIES_PATH)
public class ActivityController {
    private ActivityService activityService;

    @Autowired
    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping(value = RushHoursAppConstants.ID_PATH_PARAM)
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    public ResponseEntity<ActivityDto> get(@PathVariable int id) {
        return new ResponseEntity<>(activityService.get(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ActivityDto>> get() {
        return new ResponseEntity<>(activityService.get(), HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    public ResponseEntity<ActivityDto> create(@RequestBody ActivityDto activityDto) {
        return new ResponseEntity<>(activityService.create(activityDto), HttpStatus.CREATED);
    }

    @PatchMapping(value = RushHoursAppConstants.ID_PATH_PARAM)
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    public ResponseEntity<ActivityDto> partialUpdate(@PathVariable int id, @RequestBody Map<String, String> updates) {
        return new ResponseEntity<>(activityService.partialUpdate(id, updates), HttpStatus.OK);
    }

    @DeleteMapping(value = RushHoursAppConstants.ID_PATH_PARAM)
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        activityService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}