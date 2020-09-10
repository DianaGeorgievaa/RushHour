package com.primeholding.rushhours.controller;

import com.primeholding.rushhours.constants.RushHoursAppConstants;
import com.primeholding.rushhours.dto.ActivityDto;
import com.primeholding.rushhours.dto.AppointmentDto;
import com.primeholding.rushhours.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(RushHoursAppConstants.API_PATH + RushHoursAppConstants.APPOINTMENTS_PATH)
public class AppointmentController {
    private AppointmentService appointmentService;

    @Autowired
    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping(value = RushHoursAppConstants.ID_PATH_PARAM)
    public ResponseEntity<AppointmentDto> get(@PathVariable int id) {
        return new ResponseEntity<>(appointmentService.get(id), HttpStatus.OK);
    }

    @GetMapping(value = RushHoursAppConstants.ID_PATH_PARAM + RushHoursAppConstants.ACTIVITIES_PATH)
    public ResponseEntity<List<ActivityDto>> getActivitiesByAppointmentId(@PathVariable int id) {
        return new ResponseEntity<>(appointmentService.getActivitiesByAppointmentId(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<AppointmentDto>> get() {
        return new ResponseEntity<>(appointmentService.get(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<AppointmentDto> create(@RequestBody AppointmentDto appointmentDto) {
        return new ResponseEntity<>(appointmentService.create(appointmentDto), HttpStatus.CREATED);
    }

    @PatchMapping(value = RushHoursAppConstants.ID_PATH_PARAM)
    public ResponseEntity<AppointmentDto> partialUpdate(@PathVariable int id, @RequestBody Map<String, String> updates) {
        return new ResponseEntity<>(appointmentService.partialUpdate(id, updates), HttpStatus.OK);
    }

    @DeleteMapping(value = RushHoursAppConstants.ID_PATH_PARAM)
    public ResponseEntity<Void> delete(@PathVariable int id) {
        appointmentService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}