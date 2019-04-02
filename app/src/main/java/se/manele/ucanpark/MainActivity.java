package se.manele.ucanpark;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import se.manele.ucanpark.parkingapp.R;


/**
 * This is the activity that starts when the app opens.
 *
 * @version 2019-04-01
 * @author Elena Marzi
 */
public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Parking> parkings;
    private static final int LOCATION_REQUEST_CODE = 12345;

    /**
     * Called when the activity is starting .
     * This is where most initialization should go:
     * calling setContentView() to inflate the activity's UI,
     * using findViewById(int) to programmatically interact with widgets in the UI, etc.
     * We also read from an API and fill the UI with a list of parkings.
     * @param savedInstanceState    If the activity is being re-initialized after previously
     *                              being shut down then this Bundle contains the data it most
     *                              recently supplied in onSaveInstanceState(Bundle).
     *                              Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        Log.i("MainActivity", "After setContentView");

        parkings = new ArrayList<>();
        mAdapter = new ParkingViewAdapter(MainActivity.this, parkings);
        mRecyclerView.setAdapter(mAdapter);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i("MainActivity", "Requesting GPS permission");
            requestPermissions(new String[] { Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION }, LOCATION_REQUEST_CODE);
        }
        else {
            Log.i("MainActivity", "GPS permission is already granted");
            getLocation();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            Log.i("MainActivity", "Received GPS permission");
            getLocation();
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        Log.i("MainActivity", "Requesting location");
        // Get the location provider client
        final FusedLocationProviderClient locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // Get the last known location from the operating system
        locationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Log.i("MainActivity", "lastLocation Success");
                getApiData(location, locationProviderClient);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("MainActivity", e.getMessage());
            }
        });
    }

    private Location location = null;

    @SuppressLint("MissingPermission")
    private void getApiData(Location location, final FusedLocationProviderClient locationProviderClient) {
        if (location != null && this.location == null) {
            Log.i("Location", "LastLocation result: " + location.toString());
            double lat = location.getLatitude();
            double lng = location.getLongitude();

            //new XmlPull(getString(R.string.api_url_1), getString(R.string.api_name_1)).execute();
            new XmlPull(R.string.api_url_2, getString(R.string.api_name_2), lat, lng, 800).execute();
            new XmlPull(R.string.api_url_3, getString(R.string.api_name_3), lat, lng, 800).execute();
            new XmlPull(R.string.api_url_4, getString(R.string.api_name_4), lat, lng, 800).execute();
            new XmlPull(R.string.api_url_5, getString(R.string.api_name_5), lat, lng, 800).execute();
        }
        else if (this.location == null) {
            Log.i("Location", "Null");
            // Subscribe to location updates
            LocationRequest locationRequest = new LocationRequest()
                    .setInterval(TimeUnit.SECONDS.toMillis(1))
                    .setFastestInterval(TimeUnit.SECONDS.toMillis(1))
                    .setExpirationTime(TimeUnit.SECONDS.toMillis(60))
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            locationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult result) {
                    Log.i("LocationUpdates", "onLocationResult");
                    getApiData(result.getLastLocation(), locationProviderClient);
                }

                @Override
                public void onLocationAvailability(LocationAvailability availability) {
                    Log.i("LocationUpdates", "onLocationAvailability: " + availability.isLocationAvailable());
                }
            }, Looper.myLooper());

        }
    }

    private class XmlPull extends AsyncTask<Void, Void, ArrayList<Parking>>{
        private String url;
        private String parkingTypeName;

        public XmlPull(int stringResourceId, String parkingTypeName, double latitude, double longitude, int radius) {
            Configuration config = new Configuration(getResources().getConfiguration());
            config.setLocale(Locale.ROOT);
            Resources res = createConfigurationContext(config).getResources();
            this.url = res.getString(stringResourceId, latitude, longitude, radius);
            Log.i("XmlPull", url);
            this.parkingTypeName = parkingTypeName;
        }

        @Override
        protected ArrayList<Parking> doInBackground(Void... voids) {
            return NetworkUtils.fetchParkingData(url, parkingTypeName);
        }

        @Override
        protected synchronized void onPostExecute(ArrayList<Parking> parkings) {
            MainActivity.this.parkings.addAll(parkings);
            Collections.sort(MainActivity.this.parkings, Parking.DistanceComparator); //Compares the distances and sorts list by ascending order
            mAdapter.notifyDataSetChanged();
        }
    }
}
