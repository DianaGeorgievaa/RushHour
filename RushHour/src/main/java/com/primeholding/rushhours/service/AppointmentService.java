package com.primeholding.rushhours.service;

import com.primeholding.rushhours.dto.ActivityDto;
import com.primeholding.rushhours.dto.AppointmentDto;
import com.primeholding.rushhours.entity.Activity;
import com.primeholding.rushhours.entity.Appointment;
import com.primeholding.rushhours.entity.User;
import com.primeholding.rushhours.exception.BadRequestException;
import com.primeholding.rushhours.exception.ResourceNotFoundException;
import com.primeholding.rushhours.mapper.Mapper;
import com.primeholding.rushhours.repository.ActivityRepository;
import com.primeholding.rushhours.repository.AppointmentRepository;
import com.primeholding.rushhours.repository.UserRepository;
import com.primeholding.rushhours.security.UserPrincipal;
import com.primeholding.rushhours.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class AppointmentService {
    private static final int ADMINISTRATOR_ID = 1;

    private AppointmentRepository appointmentRepository;
    private ActivityRepository activityRepository;
    private UserRepository userRepository;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository, ActivityRepository activityRepository, UserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.activityRepository = activityRepository;
        this.userRepository = userRepository;
    }

    public AppointmentDto get(int id) throws ResourceNotFoundException {
        if (!appointmentRepository.findById(id).isPresent()) {
            throw new ResourceNotFoundException(MessageUtils.NOT_EXISTING_APPOINTMENT_MESSAGE);
        }

        return Mapper.INSTANCE.mapAppointmentToAppointmentDto(appointmentRepository.findById(id).get());
    }


    public List<AppointmentDto> get() {

        return Mapper.INSTANCE.mapListOfAppointmentsToAppointmentsDto(appointmentRepository.findAll());
    }

    public AppointmentDto create(AppointmentDto appointmentDto) throws BadRequestException, ResourceNotFoundException {
        List<Integer> activitiesId = appointmentDto.getActivitiesId();

        if (appointmentDto.getStartDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException(MessageUtils.PAST_START_DATE_MESSAGE);
        }

        if (activitiesId.isEmpty()) {
            throw new BadRequestException(MessageUtils.EMPTY_ACTIVITY_LIST_MESSAGE);
        }

        List<Activity> activities = new ArrayList<>();
        Optional<Activity> currentActivity;
        for (int id : activitiesId) {
            currentActivity = activityRepository.findById(id);
            if (!currentActivity.isPresent()) {
                throw new ResourceNotFoundException(MessageUtils.NOT_EXISTING_ACTIVITY_MESSAGE);
            }
            activities.add(currentActivity.get());
        }

        int currentUserId = getCurrentUserId();
        Optional<User> user;
        if (currentUserId == ADMINISTRATOR_ID) {
            user = userRepository.findById(appointmentDto.getUserId());

            if (!user.isPresent()) {
                throw new ResourceNotFoundException(MessageUtils.NOT_EXISTING_USER_MESSAGE);
            }
            appointmentDto.setUserId(appointmentDto.getUserId());

        } else {
            appointmentDto.setUserId(currentUserId);
        }

        Appointment appointment = Mapper.INSTANCE.mapAppointmentDtoToAppointment(appointmentDto);
        appointment.setActivities(activities);
        appointment.updateEndDate();
        List<Appointment> allAppointments = appointmentRepository.findAll();
        if (isOverlapped(appointment, allAppointments)) {
            throw new BadRequestException(MessageUtils.APPOINTMENTS_OVERLAPPING_MESSAGE);
        }

        return Mapper.INSTANCE.mapAppointmentToAppointmentDto(appointmentRepository.save(appointment));
    }

    public void delete(int id) throws ResourceNotFoundException {
        Optional<Appointment> appointment = appointmentRepository.findById(id);
        if (!appointment.isPresent()) {
            throw new ResourceNotFoundException(MessageUtils.NOT_EXISTING_APPOINTMENT_MESSAGE);
        }

        appointmentRepository.deleteById(id);
    }

    public List<ActivityDto> getActivitiesByAppointmentId(int id) throws ResourceNotFoundException {
        Optional<Appointment> appointment = appointmentRepository.findById(id);
        if (!appointment.isPresent()) {
            throw new ResourceNotFoundException(MessageUtils.NOT_EXISTING_APPOINTMENT_MESSAGE);
        }

        List<Activity> activities = appointment.get().getActivities();

        return Mapper.INSTANCE.mapListOfActivitiesToActivitiesDto(activities);
    }

    public List<AppointmentDto> findAppointmentsByUserId(int id) {
        List<Appointment> appointments = appointmentRepository.findByUserId(id);

        return Mapper.INSTANCE.mapListOfAppointmentsToAppointmentsDto(appointments);
    }

    public AppointmentDto partialUpdate(int id, Map<String, String> updates) throws BadRequestException, ResourceNotFoundException {
        Optional<Appointment> appointment = appointmentRepository.findById(id);
        if (!appointment.isPresent()) {
            throw new ResourceNotFoundException(MessageUtils.NOT_EXISTING_APPOINTMENT_MESSAGE);
        }

        String startDate = updates.get("startDate");
        if (LocalDateTime.parse(startDate).isBefore(LocalDateTime.now())) {
            throw new BadRequestException(MessageUtils.PAST_START_DATE_MESSAGE);
        }

        List<Appointment> appointmentsWithoutTheCurrent = getAllAppointmentsWithoutSpecificOne(id);
        appointment.get().setStartDate(LocalDateTime.parse(startDate));
        appointment.get().updateEndDate();

        if (isOverlapped(appointment.get(), appointmentsWithoutTheCurrent)) {
            throw new BadRequestException(MessageUtils.APPOINTMENTS_OVERLAPPING_MESSAGE);
        }

        return Mapper.INSTANCE.mapAppointmentToAppointmentDto(appointmentRepository.save(appointment.get()));
    }

    private boolean isOverlapped(Appointment appointment, List<Appointment> appointments) {
        LocalDateTime startDate = appointment.getStartDate();
        LocalDateTime endDate = appointment.getEndDate();

        if (appointments.isEmpty()) {
            return false;
        }

        boolean areOverlapped;
        for (Appointment currentAppointment : appointments) {
            areOverlapped = startDate.compareTo(currentAppointment.getStartDate()) == 0 || startDate.compareTo(currentAppointment.getEndDate()) == 0
                    || endDate.compareTo(currentAppointment.getStartDate()) == 0 || endDate.compareTo(currentAppointment.getEndDate()) == 0;

            if (startDate.isBefore(currentAppointment.getEndDate()) && currentAppointment.getStartDate().isBefore(endDate) || areOverlapped) {
                return true;
            }
        }

        return false;
    }

    private int getCurrentUserId() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return principal.getId();
    }

    private List<Appointment> getAllAppointmentsWithoutSpecificOne(int id) {
        List<Appointment> allAppointments = appointmentRepository.findAll();
        for (int index = 0; index < allAppointments.size(); index++) {
            if (allAppointments.get(index).getId() == id) {
                allAppointments.remove(allAppointments.get(index));
            }
        }

        return allAppointments;
    }
}