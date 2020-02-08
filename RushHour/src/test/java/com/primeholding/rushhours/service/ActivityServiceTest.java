package com.primeholding.rushhours.service;

import com.primeholding.rushhours.RushHoursApplication;
import com.primeholding.rushhours.dto.ActivityDto;
import com.primeholding.rushhours.entity.Activity;
import com.primeholding.rushhours.entity.Appointment;
import com.primeholding.rushhours.exception.BadRequestException;
import com.primeholding.rushhours.exception.ResourceNotFoundException;
import com.primeholding.rushhours.mapper.Mapper;
import com.primeholding.rushhours.repository.ActivityRepository;
import com.primeholding.rushhours.repository.AppointmentRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = RushHoursApplication.class)
public class ActivityServiceTest {
    private static final int EXISTING_ACTIVITY_ID = 1;
    private static final int NOT_EXISTING_ACTIVITY_ID = 2;

    private List<Activity> activities;
    private ActivityService activityService;

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private ActivityRepository activityRepository;

    @Before
    public void setUp() {
        activities = new ArrayList<>();
        activityService = new ActivityService(activityRepository, appointmentRepository);
    }

    @Test
    public void testGetActivityByExistingId() throws ResourceNotFoundException {
        List<Appointment> appointments = new ArrayList<>();
        Activity activity = new Activity("activity", 30, 50, appointments);
        activity.setId(EXISTING_ACTIVITY_ID);

        when(activityRepository.findById(EXISTING_ACTIVITY_ID)).thenReturn(java.util.Optional.of(activity));

        assertEquals(EXISTING_ACTIVITY_ID, activityService.get(EXISTING_ACTIVITY_ID).getId());
    }

    @Test(expected = ResourceNotFoundException.class)
    public void testGetActivityByNotExistingId() throws ResourceNotFoundException {
        when(activityRepository.findById(NOT_EXISTING_ACTIVITY_ID)).thenReturn(Optional.empty());

        activityService.get(NOT_EXISTING_ACTIVITY_ID);
    }

    @Test
    public void testGetAllActivitiesWhenThereAreNoActivities() {
        when(activityRepository.findAll()).thenReturn(activities);

        assertTrue(activityService.get().isEmpty());
    }

    @Test
    public void testGetAllActivitiesWhenThereAreAppointments() {
        addActivities();
        when(activityRepository.findAll()).thenReturn(activities);

        assertEquals(2, activityService.get().size());
    }

    @Test
    public void testCreateActivityWithCorrectData() throws BadRequestException {
        List<Appointment> appointments = new ArrayList<>();
        Activity activity = new Activity("activity", 30, 50, appointments);

        ActivityDto activityDto = Mapper.INSTANCE.activityToActivityDto(activity);
        activityService.create(activityDto);

        verify(activityRepository, times(1)).save(Mapper.INSTANCE.activityDtoToActivity(activityDto));
    }

    @Test(expected = BadRequestException.class)
    public void testCreateActivityWithIncorrectDuration() throws BadRequestException {
        List<Appointment> appointments = new ArrayList<>();
        Activity activity = new Activity("activity", -30, 50, appointments);

        ActivityDto activityDto = Mapper.INSTANCE.activityToActivityDto(activity);
        activityService.create(activityDto);

        verify(activityRepository, times(1)).save(Mapper.INSTANCE.activityDtoToActivity(activityDto));
    }


    @Test(expected = BadRequestException.class)
    public void testCreateActivityWithIncorrectPrice() throws BadRequestException {
        List<Appointment> appointments = new ArrayList<>();
        Activity activity = new Activity("activity", 30, -50, appointments);

        ActivityDto activityDto = Mapper.INSTANCE.activityToActivityDto(activity);
        activityService.create(activityDto);

        verify(activityRepository, times(1)).save(Mapper.INSTANCE.activityDtoToActivity(activityDto));
    }

    private void addActivities() {
        List<Appointment> appointments = new ArrayList<>();
        Activity activity = new Activity("activity1", 30, 50, appointments);
        Activity anotherActivity = new Activity("activity1", 10, 30, appointments);

        activities.add(activity);
        activities.add(anotherActivity);
    }
}
