package com.primeholding.rushhours.controller;

import com.primeholding.rushhours.dto.ActivityDto;
import com.primeholding.rushhours.dto.AppointmentDto;
import com.primeholding.rushhours.exception.BadRequestException;
import com.primeholding.rushhours.exception.ResourceNotFoundException;
import com.primeholding.rushhours.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private AppointmentService appointmentService;

    @Autowired
    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<AppointmentDto> get(@RequestParam int id) throws ResourceNotFoundException {
        return new ResponseEntity<>(appointmentService.get(id), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}/activities")
    public ResponseEntity<List<ActivityDto>> getActivitiesByAppointmentId(@PathVariable int id) throws ResourceNotFoundException {
        return new ResponseEntity<>(appointmentService.getActivitiesByAppointmentId(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<AppointmentDto>> get() {
        return new ResponseEntity<>(appointmentService.get(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<AppointmentDto> create(@RequestBody AppointmentDto appointmentDto) throws BadRequestException, ResourceNotFoundException {
        return new ResponseEntity<>(appointmentService.create(appointmentDto), HttpStatus.CREATED);
    }

    @PatchMapping(value = "/{id}")
    public ResponseEntity<AppointmentDto> partialUpdate(@RequestParam int id, @RequestBody Map<String, String> updates) throws BadRequestException, ResourceNotFoundException {
        return new ResponseEntity<>(appointmentService.partialUpdate(id, updates), HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@RequestParam int id) throws ResourceNotFoundException {
        appointmentService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}