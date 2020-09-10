package com.primeholding.rushhours.controller;

import com.primeholding.rushhours.constants.MessageConstants;
import com.primeholding.rushhours.constants.RushHoursAppConstants;
import com.primeholding.rushhours.entity.Role;
import com.primeholding.rushhours.entity.User;

import com.primeholding.rushhours.payload.response.ApiResponse;
import com.primeholding.rushhours.payload.response.JwtAuthenticationResponse;
import com.primeholding.rushhours.payload.request.LoginRequest;
import com.primeholding.rushhours.payload.request.SignUpRequest;
import com.primeholding.rushhours.security.JwtTokenProvider;
import com.primeholding.rushhours.service.RoleService;
import com.primeholding.rushhours.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping(RushHoursAppConstants.API_PATH + RushHoursAppConstants.AUTH_PATH)
public class AuthenticationController {
    private AuthenticationManager authenticationManager;
    private UserService userService;
    private RoleService roleService;
    private PasswordEncoder passwordEncoder;
    private JwtTokenProvider tokenProvider;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager,
                                    UserService userService, RoleService roleService,
                                    PasswordEncoder passwordEncoder,
                                    JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping(RushHoursAppConstants.SIGN_IN_PATH)
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    @PostMapping(RushHoursAppConstants.SIGN_UP_PATH)
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        if (userService.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity(new ApiResponse(false, MessageConstants.ALREADY_EXISTING_EMAIL),
                    HttpStatus.BAD_REQUEST);
        }

        User user = new User(signUpRequest.getFirstName(), signUpRequest.getLastName(),
                signUpRequest.getEmail(), signUpRequest.getPassword());
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Optional<Role> role = roleService.findById(1);
        user.setRole(role.get());

        User result = userService.save(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path(RushHoursAppConstants.API_PATH
                        + RushHoursAppConstants.USERS_PATH
                        + RushHoursAppConstants.EMAIL_PATH_PARAM)
                .buildAndExpand(result.getEmail()).toUri();

        return ResponseEntity.created(location).body(new ApiResponse(true, MessageConstants.SUCCESSFUL_REGISTRATION));
    }
}