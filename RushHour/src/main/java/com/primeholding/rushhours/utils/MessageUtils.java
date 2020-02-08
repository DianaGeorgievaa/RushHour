package com.primeholding.rushhours.utils;

public final class MessageUtils {

    public static final String NOT_EXISTING_ACTIVITY_MESSAGE = "Activity with the given id does not exist!";
    public static final String NEGATIVE_PRICE_AND_DURATION_MESSAGE = "The price and the duration can not be negative";
    public static final String NEGATIVE_PRICE_MESSAGE = "The price can not be negative!";
    public static final String NEGATIVE_DURATION_MESSAGE = "The duration can not be negative!";
    public static final String APPOINTMENTS_OVERLAPPING_MESSAGE = "The appointments can not overlap";
    public static final String NOT_EXISTING_APPOINTMENT_MESSAGE = "Appointment with the given id does not exist!";
    public static final String PAST_START_DATE_MESSAGE = "The start date can not be past!";
    public static final String EMPTY_ACTIVITY_LIST_MESSAGE = "The activity list can not be empty!";
    public static final String NOT_EXISTING_USER_MESSAGE = "User with the given id does not exist!";
    public static final String ALREADY_EXISTING_USER_MESSAGE = "User with the given email already exists!";
    public static final String NOT_EXISTING_ROLE_MESSAGE = "Role with the given id does not exist!";

    private MessageUtils() {
    }
}
