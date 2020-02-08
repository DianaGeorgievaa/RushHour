package com.primeholding.rushhours.mapper;

import com.primeholding.rushhours.dto.ActivityDto;
import com.primeholding.rushhours.dto.AppointmentDto;
import com.primeholding.rushhours.dto.RoleDto;
import com.primeholding.rushhours.dto.UserDto;
import com.primeholding.rushhours.entity.Activity;
import com.primeholding.rushhours.entity.Appointment;
import com.primeholding.rushhours.entity.Role;
import com.primeholding.rushhours.entity.User;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@org.mapstruct.Mapper(componentModel = "spring")
public interface Mapper {
    Mapper INSTANCE = Mappers.getMapper(Mapper.class);

    @Mapping(source = "roleId", target = "role.id")
    User userDtoToUser(UserDto userDto);

    @Mapping(source = "role.id", target = "roleId")
    UserDto userToUserDto(User user);

    List<UserDto> mapListOfUsersToUserDto(List<User> users);

    Role roleDtoToRole(RoleDto roleDto);

    RoleDto roleToRoleDto(Role role);

    List<RoleDto> mapListOfRolesToRolesDto(List<Role> roles);

    Activity activityDtoToActivity(ActivityDto activityDto);

    ActivityDto activityToActivityDto(Activity activity);

    List<ActivityDto> mapListOfActivitiesToActivitiesDto(List<Activity> activities);

    @IterableMapping(elementTargetType = Integer.class)
    List<Integer> mapListOfActivitiestoListOfId(List<Activity> activities);

    Activity mapIntegerToActivity(Integer id);

    default Integer mapActivityToInteger(Activity activity) {
        return activity.getId();
    }

    @Mapping(source = "activitiesId", target = "activities")
    @Mapping(source = "userId", target = "user.id")
    Appointment mapAppointmentDtoToAppointment(AppointmentDto AppointmentDto);

    @Mapping(source = "activities", target = "activitiesId")
    @Mapping(source = "user.id", target = "userId")
    AppointmentDto mapAppointmentToAppointmentDto(Appointment appointment);

    List<AppointmentDto> mapListOfAppointmentsToAppointmentsDto(List<Appointment> appointments);
}