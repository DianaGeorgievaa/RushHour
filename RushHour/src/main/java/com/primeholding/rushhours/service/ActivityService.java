package com.primeholding.rushhours.service;

import com.primeholding.rushhours.dto.ActivityDto;
import com.primeholding.rushhours.dto.AppointmentDto;
import com.primeholding.rushhours.entity.Activity;
import com.primeholding.rushhours.entity.Appointment;
import com.primeholding.rushhours.exception.BadRequestException;
import com.primeholding.rushhours.exception.ResourceNotFoundException;
import com.primeholding.rushhours.mapper.Mapper;
import com.primeholding.rushhours.repository.ActivityRepository;
import com.primeholding.rushhours.repository.AppointmentRepository;
import com.primeholding.rushhours.utils.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class ActivityService {
    private static final String PRICE = "price";
    private static final String DURATION = "duration";
    private static final String NAME = "name";

    private ActivityRepository activityRepository;
    private AppointmentRepository appointmentRepository;

    @Autowired
    public ActivityService(ActivityRepository activityRepository, AppointmentRepository appointmentRepository) {
        this.activityRepository = activityRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public ActivityDto get(int id) throws ResourceNotFoundException {
        Optional<Activity> activity = activityRepository.findById(id);
        if (!activity.isPresent()) {
            throw new ResourceNotFoundException(MessageUtils.NOT_EXISTING_ACTIVITY_MESSAGE);
        }

        return Mapper.INSTANCE.activityToActivityDto(activity.get());
    }

    public List<ActivityDto> get() {
        return Mapper.INSTANCE.mapListOfActivitiesToActivitiesDto(activityRepository.findAll());
    }

    public ActivityDto create(ActivityDto activityDto) throws BadRequestException {
        if (activityDto.getDuration() <= 0 || activityDto.getPrice() <= 0) {
            throw new BadRequestException(MessageUtils.NEGATIVE_PRICE_AND_DURATION_MESSAGE);
        }

        Activity activity = Mapper.INSTANCE.activityDtoToActivity(activityDto);

        return Mapper.INSTANCE.activityToActivityDto(activityRepository.save(activity));
    }

    public void delete(int id) throws ResourceNotFoundException {
        Optional<Activity> activity = activityRepository.findById(id);
        if (!activity.isPresent()) {
            throw new ResourceNotFoundException(MessageUtils.NOT_EXISTING_ACTIVITY_MESSAGE);
        }

        for (Appointment appointment : activity.get().getAppointments()) {
            appointment.removeActivity(id);
            appointmentRepository.save(appointment);

            if (appointment.getActivities().isEmpty()) {
                activityRepository.delete(activity.get());
                appointmentRepository.deleteById(appointment.getId());
            } else {
                appointment.updateEndDate();
                appointmentRepository.save(appointment);
                activityRepository.delete(activity.get());
            }
        }
    }

    public ActivityDto partialUpdate(int id, Map<String, String> updates) throws BadRequestException, ResourceNotFoundException {
        Optional<Activity> activity = activityRepository.findById(id);
        if (!activity.isPresent()) {
            throw new ResourceNotFoundException(MessageUtils.NOT_EXISTING_ACTIVITY_MESSAGE);
        }

        updatePrice(id, updates);
        updateDuration(id, updates);
        updateName(id, updates);

        return Mapper.INSTANCE.activityToActivityDto(activityRepository.save(activity.get()));
    }

    private List<Appointment> getAllAppointmentsWithoutSpecificOne(int id) {
        List<Appointment> appointments = appointmentRepository.findAll();
        for (int index = 0; index < appointments.size(); index++) {
            if (appointments.get(index).getId() == id) {
                appointments.remove(index);
                return appointments;
            }
        }
        return appointments;
    }

    private boolean isOverlapped(int id, Appointment appointment) {
        List<Appointment> appointments = getAllAppointmentsWithoutSpecificOne(id);
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

    private List<Appointment> getAppointmentsWhichContainSpecificActivity(int id, List<Appointment> allAppointments) {
        AppointmentDto currentAppointmentDto;
        Appointment appointment;
        List<Appointment> appointments = new ArrayList<>();

        for (Appointment currentAppointment : allAppointments) {
            currentAppointmentDto = Mapper.INSTANCE.mapAppointmentToAppointmentDto(currentAppointment);
            for (int i = 0; i < currentAppointmentDto.getActivitiesId().size(); i++) {
                if (id == currentAppointmentDto.getActivitiesId().get(i)) {
                    appointment = Mapper.INSTANCE.mapAppointmentDtoToAppointment(currentAppointmentDto);
                    appointments.add(appointment);
                }
            }
        }
        return appointments;
    }

    private void updatePrice(int id, Map<String, String> updates) throws BadRequestException {
        Optional<Activity> activity = activityRepository.findById(id);
        if (updates.containsKey(PRICE)) {
            if (Double.parseDouble(updates.get(PRICE)) <= 0) {
                throw new BadRequestException(MessageUtils.NEGATIVE_PRICE_MESSAGE);
            }
            activity.get().setPrice(Double.parseDouble(updates.get(PRICE)));
        }
    }

    private void updateDuration(int id, Map<String, String> updates) throws BadRequestException {
        Optional<Activity> activity = activityRepository.findById(id);
        if (updates.containsKey(DURATION)) {
            if (Integer.parseInt(updates.get(DURATION)) <= 0) {
                throw new BadRequestException(MessageUtils.NEGATIVE_DURATION_MESSAGE);
            }

            double oldDuration = activity.get().getDuration();
            LocalDateTime endDate;
            AppointmentDto currentAppointmentDto;
            List<Appointment> appointments = getAppointmentsWhichContainSpecificActivity(id, appointmentRepository.findAll());
            Appointment appointment;

            for (Appointment currentAppointment : appointments) {
                currentAppointmentDto = Mapper.INSTANCE.mapAppointmentToAppointmentDto(currentAppointment);
                endDate = currentAppointment.getEndDate();
                appointment = Mapper.INSTANCE.mapAppointmentDtoToAppointment(currentAppointmentDto);
                if (oldDuration < Integer.parseInt(updates.get(DURATION))) {
                    appointment.setEndDate(endDate.plus(((long) (Integer.parseInt(updates.get(DURATION)) - oldDuration)), ChronoUnit.MINUTES));
                } else {
                    appointment.setEndDate(endDate.minus((long) oldDuration - Integer.parseInt(updates.get(DURATION)), ChronoUnit.MINUTES));
                }

                if (isOverlapped(appointment.getId(), appointment)) {
                    throw new BadRequestException(MessageUtils.APPOINTMENTS_OVERLAPPING_MESSAGE);
                }
                appointmentRepository.save(appointment);
            }

            activity.get().setDuration(Integer.parseInt(updates.get(DURATION)));
        }
    }

    private void updateName(int id, Map<String, String> updates) {
        Optional<Activity> activity = activityRepository.findById(id);
        if (updates.containsKey(NAME)) {
            activity.get().setName(updates.get(NAME));
        }
    }
}