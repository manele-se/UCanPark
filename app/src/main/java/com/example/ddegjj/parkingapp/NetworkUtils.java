package com.example.ddegjj.parkingapp;

import android.os.Debug;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * This class is a helper class with the purpose of fetching and parsing
 * data from Göteborgs Parkering API. This class should not be instantiated.
 *
 * @author David Svensson, Daniel Duvanå, Elena Marzi
 * @version 2018-09-28
 */
public final class NetworkUtils {
    private NetworkUtils(){

    }

    /**
     * This method fetches a stream from the API URL and parses the data
     * into Parking objects inside of an ArrayList. To change the data being fetched
     * and returned, you have to change the specified URL inside the method.
     * @return  returns an ArrayList<Parking>
     */
    public static ArrayList<Parking> fetchParkingData(String api_url, String parkingName){
        Parking currParking = null;
        String currText = "";
        ArrayList<Parking> parkings = new ArrayList<>();

        // Clear the list of parkings so that the list is empty
        //before every time parkings are fetched
        parkings.clear();

        try {
            //url is located in resources
            URL url = new URL(api_url);

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            XmlPullParser xpp = factory.newPullParser();
            // Fetches the XML-file as an input stream
            xpp.setInput(url.openConnection().getInputStream(), "UTF_8");
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                // Retrieves and stores the current tag in the XML stream
                String tagname = xpp.getName();
                // Depending on what tag we are inside, we'll take different action in the following switch case structure
                switch (eventType) {

                    case XmlPullParser.START_TAG:
                        if (tagname.equalsIgnoreCase(parkingName)) {
                            currParking = new Parking();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        // Retrieves and stores the text between the tags, to be used in the next case
                        currText = xpp.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if (tagname.equalsIgnoreCase(parkingName)) {
                            // If we are at the end of the current parking tag
                            // (</PrivateParking>, we add the current parking object
                            // to the ArrayList of parkings
                            parkings.add(currParking);
                        } else if (tagname.equalsIgnoreCase("Id")) {
                            // If we are the end of the "Id" section (</Id>) we store
                            // the Id text in our current parking object
                            currParking.setId(currText);
                        } else if (tagname.equalsIgnoreCase("Name")) {
                            // If we are the end of the "name" section (</Name>) we store
                            // the name text in our current parking object
                            currParking.setName(currText);
                        } else if (tagname.equalsIgnoreCase("ParkingSpaces")) {
                            // If we are at the end of the "ParkingSpaces" section (</ParkingSpaces>)
                            // we store the number of spaces in our current parking object.
                            // IN THE FUTURE WE PROBABLY WAN'T TO CHANGE THIS TO __FREE SPACES__ (</FreeSpaces>).
                            // Right now we don't do that since almost no parkings have that data.
                            currParking.setParkingSpots(Integer.valueOf(currText));
                        } else if (tagname.equalsIgnoreCase("FreeSpaces")) {
                            // If we are the end of the "ParkingSpots" section (</ParkingSpots>) we store
                            // the ParkingSpots amount as a string in our current parking object
                            currParking.setFreeSpots(Integer.valueOf(currText));
                        } else if (tagname.equalsIgnoreCase("Parkingcost")) {
                            // If we are the end of the "ExtraInfo" section (</ExtraInfo>) we store
                            // the ExtraInfo text in our current parking object
                            //currParking.setExtraInformation(currText);
                        } else if (tagname.equalsIgnoreCase("Distance")) {
                            // If we are the end of the "Distance" section (</Distance>) we store
                            // the Distance as an int since we want to compare our current parking objects
                            currParking.setDistance(Integer.parseInt(currText));
                        } else if (tagname.equalsIgnoreCase("Lat")) {
                            // If we are the end of the "Lat" section (</Lat>) we store
                            // the Latitude as a double since we want to use it to calculate distance
                            currParking.setLatitude(Double.parseDouble(currText));
                        } else if (tagname.equalsIgnoreCase("Long")) {
                            // If we are the end of the "Long" section (</Long>) we store
                            // the longitude as a double since we want to use it to calculate distance
                            currParking.setLongitude(Double.parseDouble(currText));
                        } else if (tagname.equalsIgnoreCase("ExtraInfo")) {
                            // If we are the end of the "ExtraInfo" section (</ExtraInfo>) we store
                            // the extra information so that we can parse parking time rules
                            currParking.setExtraInformation(currText);
                        }
                            //Add more else if cases when more tags are needed, just follow the examples above.


                        break;

                    default:
                        break;
                }
                // Moves to the next element in the XML stream
                eventType = xpp.next();
            }

        } catch (XmlPullParserException | IOException e) {
            Parking p = new Parking();
            p.setName("Network error");
            parkings.add(p);
        }
        return parkings;
    }

}
