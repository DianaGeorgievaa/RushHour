package com.primeholding.rushhours.controller;

import com.primeholding.rushhours.dto.AppointmentDto;
import com.primeholding.rushhours.dto.UserDto;
import com.primeholding.rushhours.exception.ResourceAlreadyExistsException;
import com.primeholding.rushhours.exception.ResourceNotFoundException;
import com.primeholding.rushhours.model.UserModel;
import com.primeholding.rushhours.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<UserModel> get(@RequestParam int id) throws ResourceNotFoundException {
        return new ResponseEntity<>(userService.get(id), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}/appointments")
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    public ResponseEntity<List<AppointmentDto>> getAppointmentsByUserId(@PathVariable int id) throws ResourceNotFoundException {
        return new ResponseEntity<>(userService.getAppointmentsByUserId(id), HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    public ResponseEntity<List<UserDto>> get() {
        return new ResponseEntity<>(userService.get(), HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    public ResponseEntity<UserModel> create(@RequestBody UserDto userDto) throws ResourceAlreadyExistsException {
        return new ResponseEntity<>(userService.create(userDto), HttpStatus.CREATED);
    }

    @PatchMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    public ResponseEntity<UserModel> partialUpdate(@RequestParam int id, @RequestBody Map<String, String> updates) throws ResourceNotFoundException, ResourceAlreadyExistsException {
        return new ResponseEntity<>(userService.partialUpdate(id, updates), HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    public ResponseEntity<Void> delete(@RequestParam int id) throws ResourceNotFoundException {
        userService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}