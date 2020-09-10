package com.primeholding.rushhours.service;

import com.primeholding.rushhours.dto.AppointmentDto;
import com.primeholding.rushhours.dto.UserDto;
import com.primeholding.rushhours.entity.Role;
import com.primeholding.rushhours.entity.User;
import com.primeholding.rushhours.exception.ResourceAlreadyExistsException;
import com.primeholding.rushhours.exception.ResourceNotFoundException;
import com.primeholding.rushhours.mapper.Mapper;
import com.primeholding.rushhours.model.UserModel;
import com.primeholding.rushhours.repository.RoleRepository;
import com.primeholding.rushhours.repository.UserRepository;
import com.primeholding.rushhours.constants.MessageConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class UserService {
    private static final int USER_ROLE_ID = 1;
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private RoleRepository roleRepository;
    private AppointmentService appointmentService;

    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       RoleRepository roleRepository,
                       AppointmentService appointmentService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.appointmentService = appointmentService;
    }

    public UserModel get(int id) {
        Optional<User> user = userRepository.findById(id);
        if (!user.isPresent()) {
            throw new ResourceNotFoundException(MessageConstants.NOT_EXISTING_USER_MESSAGE);
        }

        return new UserModel(Mapper.INSTANCE.userToUserDto(user.get()));
    }

    public List<UserDto> get() {
        return Mapper.INSTANCE.mapListOfUsersToUserDto(userRepository.findAll());
    }

    public UserModel create(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new ResourceAlreadyExistsException(MessageConstants.ALREADY_EXISTING_USER_MESSAGE);
        }

        User user = Mapper.INSTANCE.userDtoToUser(userDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Optional<Role> role = roleRepository.findById(USER_ROLE_ID);
        user.setRole(role.get());

        return new UserModel(Mapper.INSTANCE.userToUserDto(userRepository.save(user)));
    }

    public void delete(int id) {
        Optional<User> user = userRepository.findById(id);
        if (!user.isPresent()) {
            throw new ResourceNotFoundException(MessageConstants.NOT_EXISTING_USER_MESSAGE);
        }

        appointmentService.findAppointmentsByUserId(id)
                .forEach(appointmentDto -> {
                    try {
                        appointmentService.delete(appointmentDto.getId());
                    } catch (ResourceNotFoundException e) {
                        System.out.println(e.getMessage());
                    }
                });

        userRepository.deleteById(id);
    }

    public UserModel partialUpdate(int id, Map<String, String> updates) {
        Optional<User> user = userRepository.findById(id);
        if (!user.isPresent()) {
            throw new ResourceNotFoundException(MessageConstants.NOT_EXISTING_USER_MESSAGE);
        }

        if (updates.containsKey(EMAIL)) {
            if (userRepository.existsByEmail(updates.get(EMAIL))) {
                throw new ResourceAlreadyExistsException(MessageConstants.ALREADY_EXISTING_USER_MESSAGE);
            }
            user.get().setEmail(updates.get(EMAIL));
        }

        if (updates.containsKey(PASSWORD)) {
            user.get().setPassword(passwordEncoder.encode(updates.get(PASSWORD)));
        }

        return new UserModel(Mapper.INSTANCE.userToUserDto(userRepository.save(user.get())));
    }

    public List<AppointmentDto> getAppointmentsByUserId(int id) {
        Optional<User> user = userRepository.findById(id);
        if (!user.isPresent()) {
            throw new ResourceNotFoundException(MessageConstants.NOT_EXISTING_USER_MESSAGE);
        }

        return appointmentService.findAppointmentsByUserId(id);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User save(User user) {
        return userRepository.save(user);
    }
}