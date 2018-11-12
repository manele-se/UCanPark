package com.example.ddegjj.parkingapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * This is the activity that starts when the app opens.
 *
 * @version 2018-10-04
 * @author Daniel Duvanå, David Svensson, Elena Marzi
 */
public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Parking> parkings;

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

        parkings = new ArrayList<>();
        mAdapter = new ParkingViewAdapter(MainActivity.this, parkings);
        mRecyclerView.setAdapter(mAdapter);

        //new XmlPull(getString(R.string.api_url_1), getString(R.string.api_name_1)).execute();
        new XmlPull(getString(R.string.api_url_2), getString(R.string.api_name_2)).execute();
        new XmlPull(getString(R.string.api_url_3), getString(R.string.api_name_3)).execute();
        new XmlPull(getString(R.string.api_url_4), getString(R.string.api_name_4)).execute();
        new XmlPull(getString(R.string.api_url_5), getString(R.string.api_name_5)).execute();
    }

    private class XmlPull extends AsyncTask<Void, Void, ArrayList<Parking>>{
        private String url;
        private String parkingName;

        public XmlPull(String url, String parkingName) {
            this.url = url;
            this.parkingName = parkingName;
        }

        @Override
        protected ArrayList<Parking> doInBackground(Void... voids) {
            ArrayList<Parking> parkings = null;
            parkings = NetworkUtils.fetchParkingData(url, parkingName);
            return parkings;
        }

        @Override
        protected synchronized void onPostExecute(ArrayList<Parking> parkings) {
            MainActivity.this.parkings.addAll(parkings);
            Collections.sort(MainActivity.this.parkings,Parking.DistanceComparator); //Compares the distances and sorts list by ascending order
            mAdapter.notifyDataSetChanged();
        }
    }


}
