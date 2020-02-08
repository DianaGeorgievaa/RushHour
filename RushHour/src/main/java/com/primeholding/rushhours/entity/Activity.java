package com.primeholding.rushhours.entity;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "activities")
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private int duration;
    private double price;
    @ManyToMany(mappedBy = "activities")
    private List<Appointment> appointments;

    public Activity() {
    }

    public Activity(String name, int duration, double price, List<Appointment> appointments) {
        this.name = name;
        this.duration = duration;
        this.price = price;
        this.appointments = appointments;
    }

    public Activity(Activity activityDtoToActivity) {
        this.name = activityDtoToActivity.getName();
        this.duration = activityDtoToActivity.getDuration();
        this.price = activityDtoToActivity.getPrice();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Activity activity = (Activity) o;
        return id == activity.id &&
                duration == activity.duration &&
                Double.compare(activity.price, price) == 0 &&
                Objects.equals(name, activity.name) &&
                Objects.equals(appointments, activity.appointments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, duration, price, appointments);
    }
}