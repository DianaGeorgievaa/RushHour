package com.primeholding.rushhours.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "start_date")
    private LocalDateTime startDate;
    @Column(name = "end_date")
    private LocalDateTime endDate;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToMany
    @JoinTable(
            name = "activities_appointments",
            joinColumns = {@JoinColumn(name = "appointments_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "activities_id", referencedColumnName = "id")})
    private List<Activity> activities;

    public Appointment() {
    }

    public Appointment(LocalDateTime startDate, LocalDateTime endDate, User user) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.user = user;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }

    public void updateEndDate() {
        endDate = getStartDate();
        int sum = activities.stream()
                .mapToInt(Activity::getDuration)
                .sum();

        endDate = endDate.plus(sum, ChronoUnit.MINUTES);
    }

    public void removeActivity(int id) {
        activities.remove(getActivity(id));
    }

    public Activity getActivity(Integer id) {
        return activities.stream()
                .filter(activity -> id == activity.getId())
                .findAny()
                .orElse(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Appointment that = (Appointment) o;
        return id == that.id &&
                Objects.equals(startDate, that.startDate) &&
                Objects.equals(endDate, that.endDate) &&
                Objects.equals(user, that.user) &&
                Objects.equals(activities, that.activities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, startDate, endDate, user, activities);
    }
}