package com.primeholding.rushhours.service;

import com.primeholding.rushhours.dto.RoleDto;
import com.primeholding.rushhours.entity.Role;
import com.primeholding.rushhours.exception.ResourceNotFoundException;
import com.primeholding.rushhours.mapper.Mapper;
import com.primeholding.rushhours.repository.RoleRepository;
import com.primeholding.rushhours.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class RoleService {
    private RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public RoleDto get(int id) throws ResourceNotFoundException {
        Optional<Role> role = roleRepository.findById(id);
        if (!role.isPresent()) {
            throw new ResourceNotFoundException(MessageUtils.NOT_EXISTING_ROLE_MESSAGE);
        }

        return Mapper.INSTANCE.roleToRoleDto(role.get());
    }

    public List<RoleDto> get() {
        return Mapper.INSTANCE.mapListOfRolesToRolesDto(roleRepository.findAll());
    }

    public RoleDto create(RoleDto roleDto) {
        Role role = Mapper.INSTANCE.roleDtoToRole(roleDto);

        return Mapper.INSTANCE.roleToRoleDto(roleRepository.save(role));
    }

    public void delete(int id) throws ResourceNotFoundException {
        Optional<Role> role = roleRepository.findById(id);
        if (!role.isPresent()) {
            throw new ResourceNotFoundException(MessageUtils.NOT_EXISTING_ROLE_MESSAGE);
        }

        roleRepository.deleteById(id);
    }

    public Optional<Role> findById(int id) {
        return roleRepository.findById(id);
    }
}