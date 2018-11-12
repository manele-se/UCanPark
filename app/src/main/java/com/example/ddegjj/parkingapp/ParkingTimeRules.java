package com.example.ddegjj.parkingapp;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @version: 2018-10-04
 * @author: Elena Marzi
 *
 * This class is resposable for handling which rules controls when a parking is forbidden
 */


public class ParkingTimeRules {
    //forbidden weekdays
    private Set<DayOfWeek> weekdays = new HashSet<>();

    // forbidden timeperiod during a day (ex. 07.30 to 11.00 startHour = 7, startMinutes = 30 ..)
    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;

    // forbidden odd or even week
    private boolean forbiddenOddWeeks;
    private boolean forbiddenEvenWeeks;

    // forbidden month and day (ex. 3 March to 15 April startMonth = 3, startDay = 15, ...)
    private int startMonth;
    private int startDay;
    private int endMonth;
    private int endDay;

    /**
     *  constructor
     * @param dayOfWeek
     * @param startHour
     * @param startMinute
     * @param endHour
     * @param endMinute
     * @param forbiddenOddWeeks
     * @param forbiddenEvenWeeks
     * @param startMonth
     * @param startDay
     * @param endMonth
     * @param endDay
     */
    public ParkingTimeRules(DayOfWeek dayOfWeek, int startHour, int startMinute, int endHour, int endMinute, boolean forbiddenOddWeeks, boolean forbiddenEvenWeeks, int startMonth, int startDay, int endMonth, int endDay) {
        weekdays.add(dayOfWeek);
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
        this.forbiddenOddWeeks = forbiddenOddWeeks;
        this.forbiddenEvenWeeks = forbiddenEvenWeeks;
        this.startMonth = startMonth;
        this.startDay = startDay;
        this.endMonth = endMonth;
        this.endDay = endDay;
    }

    /**
     * add another day of the week which is forbiddden
     * @param dayOfWeek
     */
    public void addWeekDay(DayOfWeek dayOfWeek) {
        weekdays.add(dayOfWeek);
    }

    /**
     * This function answer the question is parking forbidden?
     * @param timestamp it takes the curren time and date
     * @return true if parking is forbidden at this time otherwise false
     */

    public boolean isParkingForbidden(LocalDateTime timestamp) {
        LocalDate today = LocalDate.from(timestamp);    // ex. 2018-10-03
        LocalTime now = LocalTime.from(timestamp);      //  ex. 06:19


        DayOfWeek dayOfWeek = today.getDayOfWeek();
        if (!weekdays.contains(dayOfWeek))
            return false;

        LocalTime timePeriodStart = LocalTime.of(startHour, startMinute);
        LocalTime timePeriodEnd = LocalTime.of(endHour, endMinute);
        if (now.isBefore(timePeriodStart))
            return false;
        if (now.isAfter(timePeriodEnd))
            return false;

        int weekNumber = today.get(WeekFields.ISO.weekOfYear());
        boolean oddWeek = (weekNumber % 2) == 1;
        //check if forbidden week number
        if (oddWeek && !forbiddenOddWeeks)
            return false;
        if (!oddWeek && !forbiddenEvenWeeks)
            return false;

        LocalDate datePeriodStart = today.withMonth(startMonth).withDayOfMonth(startDay);
        LocalDate datePeriodEnd = today.withMonth(endMonth).withDayOfMonth(endDay);
        if (today.isBefore(datePeriodStart))
            return false;
        if (today.isAfter(datePeriodEnd))
            return false;

        return true;
    }
}
