package se.manele.ucanpark;

import android.support.annotation.NonNull;

import org.joda.time.DateTimeConstants;
import org.joda.time.Instant;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is responsible to handle data concerning a parking spot.
 *
 * @version 2019-04-01
 * @author Elena Marzi
 */

public class Parking {
    private String id;
    private String name;
    private String extraInformation;
    private Integer parkingspots;
    private int distance;
    private double latitude;
    private double longitude;
    private ParkingTimeRules rules;
    private Integer freeSpots;

    public Parking() {

    }

    /**
     * set id and test data - phase 1-
     * id comes from API and it is uses to retrive information and set the rules
     * Those parking with no rules about time are not included
     * @param id
     */
    public void setId (String id) {
        this.id = id;
/*
        // Stigbergsliden 853m: Forbidden thursday, 02.00 to 07.00, even weeks all year
        //THIS PARKING IS USED TO DEMONSTRATE THE FORBIDDEN PARKING
        if (id.equals("1480 2016-00506")) {
            rules = new ParkingTimeRules(DayOfWeek.THURSDAY, 8, 0, 20, 0, true, true, 1, 1, 12, 31);
            rules.addWeekDay(DayOfWeek.MONDAY);
            rules.addWeekDay(DayOfWeek.TUESDAY);
            rules.addWeekDay(DayOfWeek.WEDNESDAY);
            rules.addWeekDay(DayOfWeek.THURSDAY);
            rules.addWeekDay(DayOfWeek.FRIDAY);

        }

        // Stigbergsliden 854m: Forbidden thursday, 02.00 to 07.00, even weeks all year
        //THIS PARKING IS USED TO TEST FREE SPACES: PARKING IS FULL
        if (id.equals("1480 2016-00509")) {
            freeSpots = 0;
            rules = new ParkingTimeRules(DayOfWeek.THURSDAY, 2, 0, 7, 0, false, true, 1, 1, 12, 31);
        }



        // Stigbergsliden 880m: Forbidden wednesday, 02.00 to 07.00, all weeks all year
        //THIS PARKING IS USED TO TEST FREE SPACES: 2 FREE SPOTS
        if (id.equals("1480 2007-01060")) {
            freeSpots = 2;
            rules = new ParkingTimeRules(DayOfWeek.WEDNESDAY, 2, 0, 7, 0, true, true, 1, 1, 12, 31);
        }

        // Stigbergsliden 887m: Forbidden wednesday, 02.00 to 07.00, all weeks all year
        if (id.equals("1480 2013-01014")) {
            freeSpots = 12;
            rules = new ParkingTimeRules(DayOfWeek.WEDNESDAY, 2, 0, 7, 0, true, true, 1, 1, 12, 31);
        }

        // Bläsgatan 924m: Forbidden thursday, 02.00 to 07.00, even weeks all year
        if (id.equals("1480 2013-01014")) {
            rules = new ParkingTimeRules(DayOfWeek.THURSDAY, 2, 0, 7, 0, false, true, 1, 1, 12, 31);
        }

        // Stigsbergstorget 929m: Forbidden thursday, 02.00 to 07.00, even weeks all year
        if (id.equals("1480 2007-00531")) {
            rules = new ParkingTimeRules(DayOfWeek.THURSDAY, 2, 0, 7, 0, false, true, 1, 1, 12, 31);
        }


*/


    }

    public String getId() {
        return id;
    }

    public enum ParkingStatus
    {
        SPOTS_AVAILABLE,
        PARKING_ALLOWED,
        PARKING_FORBIDDEN,
        PARKING_FULL
    }

    /** Private helper method for parkingAllowed, returns one of four possible statuses
     * of a parking lot. These four statuses can be used in new ways
     * to expand the app in the future
     *
     * @return ParkingStatus one of four possible statuses, from ParkingStatus enum.
     */
    public ParkingStatus getParkingStatus() {
        if (rules != null && rules.isParkingForbidden(Instant.now())) {
            return ParkingStatus.PARKING_FORBIDDEN;
        }
        if (freeSpots == null) {
            return ParkingStatus.PARKING_ALLOWED;
        }
        if (freeSpots == 0) {
            return ParkingStatus.PARKING_FULL;
        }
        return ParkingStatus.SPOTS_AVAILABLE;
    }

    /** This method checks if parking is allowed on this parking lot at this time
     *
     * @return true if parking is allowed, false otherwise
     */
    private boolean isParkingAllowed(){
        ParkingStatus s = getParkingStatus();
        switch(s) {
            case SPOTS_AVAILABLE:
                return true;
            case PARKING_ALLOWED:
                return true;
            case PARKING_FORBIDDEN:
                return false;
            case PARKING_FULL:
                return false;
        }
        return false;
    }

    /**
     * Method for retrieving the name of a parking object
     * @return the name of the parking object
     */
    public String getName(){
        return name;
    }

    /**
     * Method for setting the name of a parking object
     * @param name
     */
    public void setName(String name){
        this.name = name;
    }

    /**
     * Method for getting extra information of a parking spot
     * @return extra information
     */
    public String getExtraInformation() {
        return extraInformation;
    }

    /**
     * Method for setting the distance of a parking object
     * @param extraInformation
     */
    public void setExtraInformation(String extraInformation) {
        this.extraInformation = extraInformation;
        Matcher match = getMatcher(extraInformation);

        if (match.find()) {
            String weekday = match.group(1);
            int day = getDayOfWeek(weekday);

            int startHour = Integer.parseInt(match.group(2), 10);
            int startMinute = Integer.parseInt(match.group(3), 10);
            int endHour = Integer.parseInt(match.group(4), 10);
            int endMinute = Integer.parseInt(match.group(5), 10);

            boolean odd, even;

            /* if there is something after the time,
             * check the value of "jämna|udda"
             */
            /*
            even = match.group(6) == null || match.group(7).equals("jämna");
            odd  = match.group(6) == null || match.group(7).equals("udda");
            */
            if (match.group(6) != null) {
                if (match.group(7).equals("jämna")) {
                    even = true;
                    odd = false;
                }
                else {
                    odd = true;
                    even = false;
                }
            }
            else {
                odd = true;
                even = true;
            }

            int startMonth, startDay, endMonth, endDay;
            // if there is information after the odd/even week, check those values
            if (match.group(8) != null) {
                startDay = Integer.parseInt(match.group(9), 10);
                startMonth = getMonth(match.group(10));
                endDay = Integer.parseInt(match.group(11), 10);
                endMonth = getMonth(match.group(12));
            }
            else {
                startDay = 1;
                startMonth = 1;
                endDay = 31;
                endMonth = 12;
            }

            ParkingTimeRules rule = new ParkingTimeRules(day, startHour, startMinute, endHour, endMinute, odd, even, startMonth, startDay, endMonth, endDay);
            this.rules = rule;
        }
    }

    /** This method creates a regular expression and matches it with the extraInformation for a parking
     *
     */
    @NonNull
    private Matcher getMatcher(String extraInformation) {
        /*                                         1*/
        Pattern regEx = Pattern.compile("^P-förbud (.*)dagar klockan " +
              /* 2        3          4        5 */
                "(\\d{2}).(\\d{2}) - (\\d{2}).(\\d{2})" +
              /* 6 7 */
                "( (jämna|udda) veckor)?" +
              /* 8             9               10         11              12 */
                "( under tiden (\\d{1,2}):[ae] ([a-z]*) - (\\d{1,2}):[ae] ([a-z]*))?");

        return regEx.matcher(extraInformation);
    }

    private int getMonth(String group) {
        return group.equals("januari")  ?  1 :
               group.equals("februari") ?  2 :
               group.equals("mars")     ?  3 :
               group.equals("april")    ?  4 :
               group.equals("maj")      ?  5 :
               group.equals("juni")     ?  6 :
               group.equals("juli")     ?  7 :
               group.equals("augusti")  ?  8 :
               group.equals("september")?  9 :
               group.equals("oktober")  ? 10 :
               group.equals("november") ? 11 :
                                          12;
    }

    @NonNull
    private int getDayOfWeek(String weekday) {
        return weekday.equals("mån") ? DateTimeConstants.MONDAY :
               weekday.equals("tis") ? DateTimeConstants.TUESDAY :
               weekday.equals("ons") ? DateTimeConstants.WEDNESDAY :
               weekday.equals("tors")? DateTimeConstants.THURSDAY :
               weekday.equals("fre") ? DateTimeConstants.FRIDAY :
               weekday.equals("lör") ? DateTimeConstants.SATURDAY :
                                       DateTimeConstants.SUNDAY;
    }

    /**
     * Method for getting how many free spots are available in a parking spot
     * @return free spots
     */
    public Integer getFreeSpots() {
        return freeSpots;
    }

    /**
     * Method for setting the quantity of available spots in a parking object
     * @param freeSpots
     */
    public void setFreeSpots(Integer freeSpots) {
        this.freeSpots = freeSpots;
    }

    /**
     * Method for getting the total amount of spots in a parking lot
     * @return parking spots
     */
    public Integer getParkingSpots() {
        return parkingspots;
    }

    /**
     * Method for setting the quantity of total spots in a parking object
     * @param parkingspots
     */
    public void setParkingSpots(Integer parkingspots) {
        this.parkingspots = parkingspots;
    }

    /**
     * Method for getting the distance from your location to a parking lot
     * @return distance
     */
    public int getDistance() {
        return distance;
    }

    /**
     * Method for setting the distance from your location to a parking lot
     * @param distance
     */
    public void setDistance(int distance) {
        this.distance = distance;
    }

    /**
     * Method for getting the latitude of a parking lot
     * @return latitude
     */
    public double getLatitude() { return latitude; }

    /**
     * Method for setting the latitude of a parking lot
     * @param latitude
     */

    public void setLatitude(double latitude) { this.latitude = latitude; }

    /**
     * Method for getting the longitude of a parking lot
     * @return longitude
     */
    public double getLongitude() { return longitude; }

    /**
     * Method for setting the longitude of a parking lot
     * @param longitude
     */

    public void setLongitude(double longitude){ this.longitude = longitude; }



    /**
     * Method for comparing the distance between two parking objects, this will allow us to
     * sort the list by distance from our location. If distance is 0 (or equal for all objects)
     * then the order will be the same as before the sort. Sorts in ascending order.
     * @param Parking
     */
    public static Comparator<Parking> DistanceComparator = new Comparator<Parking>() {
        public int compare(Parking p1, Parking p2) {
            if (p1.freeSpots != null && p2.freeSpots == null) {
                return -1;
            }

            if (p2.freeSpots != null && p1.freeSpots == null) {
                return 1;
            }


            int d1 = p1.getDistance();
            int d2 = p2.getDistance();
            return d1 - d2;
        }};


}