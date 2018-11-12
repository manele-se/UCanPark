package com.example.ddegjj.parkingapp;


import org.junit.Test;

import static org.junit.Assert.*;

/**
 * This class is responsable to handle data concerning a parking spot.
 *
 * @version: 2018-10-09
 * @author: Johannes Magnusson

/**
 * A class to test the constraints such as if the background is red if there's no free spots and so on..
 */
public class ParkingTest {

    /**
     * This method contains test cases that test return values of methods in Parking class
     */
    @Test
    public void testReturnValues() {


        Parking p = new Parking();
        p.setId("1480 2016-00509");
        Integer zeroFreeSpots = 0;
        Integer twoFreeSpots = 2;


        assertEquals("1480 2016-00509", p.getId());
        assertNotNull(p);
        assertEquals(zeroFreeSpots, p.getFreeSpots()); //check if getFreeSpots returns 0.
        assertFalse(p.isParkingAllowed()); //0 free spots -> not allowed! -> isParkingAllowed() returns false
        p.setFreeSpots(twoFreeSpots); //Add 2 free spots
        assertTrue(p.isParkingAllowed()); //2 free spots -> allowed -> return true


        Parking p2 = new Parking();
        p2.setId("1480 2007-01060");
        assertNotNull(p2);
        assertEquals("1480 2007-01060", p2.getId());
        assertTrue(p2.isParkingAllowed()); //2 free spots -> parking allowed -> isParkingAllowed() returns true
        p2.setFreeSpots(zeroFreeSpots); //Set free spots to zero
        assertFalse(p2.isParkingAllowed()); //0 free spots -> not allowed -> isParkingAllowed() returns false


        Parking p3 = new Parking();
        assertNotNull(p3);
        p3.setId("1480 2013-01014");
        assertEquals("1480 2013-01014", p3.getId());
        p3.setFreeSpots(twoFreeSpots); //Set free spots to 2
        assertTrue(p3.isParkingAllowed()); //2 free spots -> allowed -> isParkingAllowed() returns true


        //...


        //...

    }




}