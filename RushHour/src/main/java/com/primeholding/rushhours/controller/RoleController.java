package com.primeholding.rushhours.controller;

import com.primeholding.rushhours.constants.RushHoursAppConstants;
import com.primeholding.rushhours.dto.RoleDto;
import com.primeholding.rushhours.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(RushHoursAppConstants.API_PATH + RushHoursAppConstants.ROLES_PATH)
public class RoleController {
    private RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping(value = RushHoursAppConstants.ID_PATH_PARAM)
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    public ResponseEntity<RoleDto> get(@PathVariable int id) {
        return new ResponseEntity<>(roleService.get(id), HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    public ResponseEntity<List<RoleDto>> get() {
        return new ResponseEntity<>(roleService.get(), HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    public ResponseEntity<RoleDto> create(@RequestBody RoleDto roleDto) {
        return new ResponseEntity<>(roleService.create(roleDto), HttpStatus.CREATED);
    }

    @DeleteMapping(value = RushHoursAppConstants.ID_PATH_PARAM)
    @PreAuthorize("hasRole('ROLE_ADMINISTRATOR')")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        roleService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
