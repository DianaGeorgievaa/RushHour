package com.primeholding.rushhours.service;

import com.primeholding.rushhours.RushHoursApplication;
import com.primeholding.rushhours.exception.BadRequestException;
import com.primeholding.rushhours.exception.ResourceNotFoundException;
import com.primeholding.rushhours.mapper.Mapper;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.primeholding.rushhours.dto.AppointmentDto;
import com.primeholding.rushhours.entity.Appointment;
import com.primeholding.rushhours.repository.ActivityRepository;
import com.primeholding.rushhours.repository.AppointmentRepository;
import com.primeholding.rushhours.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = RushHoursApplication.class)
public class AppointmentServiceTest {
    private static final int APPOINTMENT_ID = 1;
    private static final int ACTIVITY_ID = 1;
    private static final int USER_ID = 1;
    private static final int EXISTING_ID = 2;
    private static final int NOT_EXISTING_ID = 3;

    private List<Appointment> appointments;
    private AppointmentService appointmentService;
    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private ActivityRepository activityRepository;
    @Mock
    private UserRepository userRepository;

    @Before
    public void setUp() {
        appointments = new ArrayList<>();
        appointmentService = new AppointmentService(appointmentRepository, activityRepository, userRepository);
    }

    @Test
    public void testGetAllAppointmentsWhenThereAreNoAppointments() {
        when(appointmentRepository.findAll()).thenReturn(appointments);

        assertTrue(appointmentService.get().isEmpty());
    }

    @Test
    public void testGetAllAppointmentsWhenThereAreAppointments() {
        addAppointments();
        when(appointmentRepository.findAll()).thenReturn(appointments);

        assertEquals(2, appointmentService.get().size());
    }

    @Test
    public void testGetAppointmentByExistingId() throws ResourceNotFoundException {
        Appointment appointment = new Appointment();
        appointment.setId(EXISTING_ID);

        when(appointmentRepository.findById(EXISTING_ID)).thenReturn(java.util.Optional.of(appointment));

        assertEquals(EXISTING_ID, appointmentService.get(EXISTING_ID).getId());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetAppointmentByNotExistingId() throws ResourceNotFoundException {
        when(appointmentRepository.findById(NOT_EXISTING_ID)).thenReturn(Optional.empty());

        appointmentService.get(NOT_EXISTING_ID);
    }

    @Test(expected = BadRequestException.class)
    public void testCreateAppointmentWithEmptyActivityList() throws BadRequestException, ResourceNotFoundException {
        LocalDateTime startDate = LocalDateTime.now();
        List<Integer> activitiesId = new ArrayList<>();

        AppointmentDto appointmentDto = new AppointmentDto(APPOINTMENT_ID, startDate, startDate, USER_ID, activitiesId);
        appointmentService.create(appointmentDto);

        verify(appointmentRepository, times(1)).save(Mapper.INSTANCE.mapAppointmentDtoToAppointment(appointmentDto));
    }

    @Test(expected = BadRequestException.class)
    public void testCreateAppointmentWithPastDateForStartDate() throws BadRequestException, ResourceNotFoundException {
        LocalDateTime startDate = LocalDateTime.of(2018, 12, 12, 12, 30);
        List<Integer> activitiesId = new ArrayList<>();
        activitiesId.add(ACTIVITY_ID);

        AppointmentDto appointmentDto = new AppointmentDto(APPOINTMENT_ID, startDate, startDate, USER_ID, activitiesId);
        appointmentService.create(appointmentDto);

        verify(appointmentRepository, times(1)).save(Mapper.INSTANCE.mapAppointmentDtoToAppointment(appointmentDto));
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testCreateAppointmentWithNotExistingActivity() throws BadRequestException, ResourceNotFoundException {
        LocalDateTime startDate = LocalDateTime.of(2020, 01, 27, 12, 30);
        List<Integer> activitiesId = new ArrayList<>();
        activitiesId.add(ACTIVITY_ID);

        AppointmentDto appointmentDto = new AppointmentDto(APPOINTMENT_ID, startDate, startDate, USER_ID, activitiesId);
        appointmentService.create(appointmentDto);

        verify(appointmentRepository, times(1)).save(Mapper.INSTANCE.mapAppointmentDtoToAppointment(appointmentDto));
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testDeleteNotExistingAppointment() throws ResourceNotFoundException {
        when(appointmentRepository.findById(NOT_EXISTING_ID)).thenReturn(Optional.empty());
        appointmentService.delete(NOT_EXISTING_ID);

        verify(appointmentRepository, times(0)).deleteById(NOT_EXISTING_ID);
    }

    @Test
    public void testDeleteExistingAppointment() throws ResourceNotFoundException {
        Appointment appointment = new Appointment();
        when(appointmentRepository.findById(APPOINTMENT_ID)).thenReturn(Optional.of(appointment));
        appointmentService.delete(APPOINTMENT_ID);

        verify(appointmentRepository, times(1)).deleteById(APPOINTMENT_ID);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetActivitiesByNotExistingAppointmentId() throws ResourceNotFoundException {
        when(appointmentRepository.findById(NOT_EXISTING_ID)).thenReturn(Optional.empty());
        appointmentService.getActivitiesByAppointmentId(NOT_EXISTING_ID);

        verify(appointmentRepository, times(1)).findById(NOT_EXISTING_ID);
    }


    @Test(expected = ResourceNotFoundException.class)
    public void testPartialUpdateNotExistingAppointment() throws BadRequestException, ResourceNotFoundException {
        Map<String, String> updates = new HashMap<>();

        when(appointmentRepository.findById(NOT_EXISTING_ID)).thenReturn(Optional.empty());
        appointmentService.partialUpdate(NOT_EXISTING_ID, updates);

        verify(appointmentRepository, times(1)).findById(NOT_EXISTING_ID);
    }

    @Test(expected = BadRequestException.class)
    public void testPartialUpdateExistingAppointmentWithPastStartDate() throws BadRequestException, ResourceNotFoundException {
        Map<String, String> updates = new HashMap<>();
        updates.put("startDate", "2017-01-10T18:20:24");
        Appointment appointment = new Appointment();
        appointment.setId(APPOINTMENT_ID);

        when(appointmentRepository.findById(APPOINTMENT_ID)).thenReturn(Optional.of(appointment));
        appointmentService.partialUpdate(APPOINTMENT_ID, updates);

        verify(appointmentRepository, times(1)).findById(APPOINTMENT_ID);
    }

    private void addAppointments() {
        Appointment appointment = new Appointment();

        appointments.add(appointment);
        appointments.add(appointment);
    }
}
