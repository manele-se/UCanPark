package se.manele.ucanpark;

import org.joda.time.DateTimeFieldType;
import org.joda.time.Instant;

import java.util.HashSet;
import java.util.Set;

/**
 * @version: 2019-04-01
 * @author: Elena Marzi
 *
 * This class is responsible for handling which rules controls when a parking is forbidden
 */


public class ParkingTimeRules {
    //forbidden weekdays
    private Set<Integer> weekdays = new HashSet<>();

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
    public ParkingTimeRules(int dayOfWeek, int startHour, int startMinute, int endHour, int endMinute, boolean forbiddenOddWeeks, boolean forbiddenEvenWeeks, int startMonth, int startDay, int endMonth, int endDay) {
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
    public void addWeekDay(int dayOfWeek) {
        weekdays.add(dayOfWeek);
    }

    /**
     * This function answer the question is parking forbidden?
     * @param timestamp it takes the curren time and date
     * @return true if parking is forbidden at this time otherwise false
     */

    public boolean isParkingForbidden(Instant timestamp) {
        int dayOfWeek = timestamp.get(DateTimeFieldType.dayOfWeek());
        int now = timestamp.get(DateTimeFieldType.clockhourOfDay()) * 60 +
                  timestamp.get(DateTimeFieldType.minuteOfHour());
        if (!weekdays.contains(dayOfWeek))
            return false;

        int timePeriodStart = startHour * 60 + startMinute;
        int timePeriodEnd = endHour * 60 + endMinute;
        if (timePeriodStart < timePeriodEnd) {
            if (now < timePeriodStart || now >= timePeriodEnd) {
                return false;
            }
        }
        else {
            if (now >= timePeriodStart && now < timePeriodEnd) {
                return false;
            }
        }

        int weekNumber = timestamp.get(DateTimeFieldType.weekOfWeekyear());
        boolean oddWeek = (weekNumber % 2) == 1;
        //check if forbidden week number
        if (oddWeek && !forbiddenOddWeeks)
            return false;
        if (!oddWeek && !forbiddenEvenWeeks)
            return false;

        int today = timestamp.get(DateTimeFieldType.monthOfYear()) * 100 + timestamp.get(DateTimeFieldType.dayOfMonth());
        int datePeriodStart = startMonth * 100 + startDay;
        int datePeriodEnd = endMonth * 100 + endDay;

        if (datePeriodStart < datePeriodEnd) {
            if (today < datePeriodStart || today >= datePeriodEnd) {
                return false;
            }
        }
        else {
            if (today >= datePeriodStart && today < datePeriodEnd) {
                return false;
            }
        }

        return true;
    }
}
