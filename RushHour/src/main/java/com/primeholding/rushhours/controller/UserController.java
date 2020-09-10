package com.primeholding.rushhours.controller;

import com.primeholding.rushhours.constants.RushHoursAppConstants;
import com.primeholding.rushhours.dto.AppointmentDto;
import com.primeholding.rushhours.dto.UserDto;
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
@RequestMapping(RushHoursAppConstants.API_PATH + RushHoursAppConstants.USERS_PATH)
public class UserController {
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = RushHoursAppConstants.ID_PATH_PARAM)
    public ResponseEntity<UserModel> get(@PathVariable int id) {
        return new ResponseEntity<>(userService.get(id), HttpStatus.OK);
    }

    @GetMapping(value = RushHoursAppConstants.ID_PATH_PARAM + RushHoursAppConstants.APPOINTMENTS_PATH)
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    public ResponseEntity<List<AppointmentDto>> getAppointmentsByUserId(@PathVariable int id) {
        return new ResponseEntity<>(userService.getAppointmentsByUserId(id), HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    public ResponseEntity<List<UserDto>> get() {
        return new ResponseEntity<>(userService.get(), HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    public ResponseEntity<UserModel> create(@RequestBody UserDto userDto) {
        return new ResponseEntity<>(userService.create(userDto), HttpStatus.CREATED);
    }

    @PatchMapping(value = RushHoursAppConstants.ID_PATH_PARAM)
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    public ResponseEntity<UserModel> partialUpdate(@PathVariable int id, @RequestBody Map<String, String> updates) {
        return new ResponseEntity<>(userService.partialUpdate(id, updates), HttpStatus.OK);
    }

    @DeleteMapping(value = RushHoursAppConstants.ID_PATH_PARAM)
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        userService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}