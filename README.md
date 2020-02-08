# RushHour

## Overview

Rush Hour is appointment scheduling software. It can be used in a variety of areas, i.e. medical services, beauty and wellness, sport.

## Specifications
The application allows clients to make appointments for a given set of activities.

## Activity
- An entity that represents an activity in a certain area. It consists of:
  - Name;
  - Duration;
  - Price;
  - Appointments;

## Appointment
- An entity that represents the user appointment. The many-to-many relationship allows a user to include one or more activities in his appointment. It consists of:
  - Start date;
  - End date;
  - User;
  - Activities;

## User
- An entity that represents user information. It consists of:
  - First name;
  - Last name;
  - Email;
  - Password;
  - Roles;
  - Appointments;

## Role
- Used to create different types of users with their own level of access to the application features. It consists of:
  - Name;
  - Users;

## User Stories
- Administrator
As an administrator I would like to be able to create, read, update and delete appointments. And to create, read, update and delete activities.
- User
As a user I would like to be able to create, read, update, cancel my appointments.
