package com.primeholding.rushhours.dto;

import java.time.LocalDateTime;
import java.util.List;

public class AppointmentDto {
    private int id;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private int userId;
    private List<Integer> activitiesId;

    public AppointmentDto() {
    }

    public AppointmentDto(int id, LocalDateTime startDate, LocalDateTime endDate, int userId, List<Integer> activitiesId) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.userId = userId;
        this.activitiesId = activitiesId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<Integer> getActivitiesId() {
        return activitiesId;
    }

    public void setActivitiesId(List<Integer> activitiesId) {
        this.activitiesId = activitiesId;
    }
}