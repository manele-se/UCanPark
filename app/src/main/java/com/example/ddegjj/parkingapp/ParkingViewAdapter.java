package com.example.ddegjj.parkingapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import static android.support.v4.content.ContextCompat.getColor;


/**
 * @Version 2018-10-11
 * @author Elena Marzi, Daniel Duvanå, David Svensson, Johannes Magnusson, Gustaf Lindqvist, Johan Yngvesson
 * This class is an adapter. Adapters provide a binding from an app-specific data set
 * to views that are displayed within a View, in this case a RecyclerView.
 */
public class ParkingViewAdapter extends RecyclerView.Adapter<ParkingViewAdapter.ParkingViewHolder> {
    private List<Parking> mDataset;
    private LayoutInflater mInflater;
    private Context context;

    /**
     * This class is a ViewHolder. A ViewHolder describes an item view (RecyclerView is a list of items)
     * and metadata about its place within the RecyclerView.
     * It provides a reference to the views for each data item
     * and it provides access to all the views
     */
    public static class ParkingViewHolder extends RecyclerView.ViewHolder {
        //These are references to Views that are part of an item
        public final TextView mTextViewName;
        public final TextView mTextViewPark;
        public final TextView mTextViewInfo;
        public final TextView mTextViewDist;
        public final ConstraintLayout mParkingColor;
        public final ImageView mSign;
        public double latitude,longitude;

        /**
         * The constructor of ParkingViewHolder
         * @param itemView  An itemView, a.k.a the view of an item in the list.
         *                  In our case this comes from the XML layout "parking_view"
         */
        public ParkingViewHolder(View itemView) {
            super(itemView);
            mTextViewName = itemView.findViewById(R.id.name);
            mTextViewPark = itemView.findViewById(R.id.parkingSpots);
            mTextViewInfo = itemView.findViewById(R.id.extraInfo);
            mTextViewDist = itemView.findViewById(R.id.distance);
            mParkingColor = itemView.findViewById(R.id.parkingColor);
            mSign         = itemView.findViewById(R.id.parkingSign);

        }
    }

    /**
     * The constructor of ParkingViewAdapter
     * @param context   Interface to global information about an application environment.
     * @param myDataset The data set to be bound to the views.
     *                  In our case it's a list of Parking objects.
     */
    public ParkingViewAdapter(Context context, List<Parking> myDataset) {
        mDataset = myDataset;
        this.context = context;

        //create inflator to create instances of the parking view layout
        this.mInflater = LayoutInflater.from(context);
    }

    /**
     * This is called right when the adapter is created and is used
     * to initialize our view holder.
     * @param parent    The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType  The view type of the new View. Currently we only have 1 view type (Google it to learn more)
     * @return the new view holder
     */
    @Override
    public ParkingViewAdapter.ParkingViewHolder onCreateViewHolder(ViewGroup parent,
                                                                   int viewType) {
        // create a new view
        View itemView = mInflater.inflate(R.layout.parking_view, parent, false);
        final ParkingViewHolder vh = new ParkingViewHolder(itemView);

        itemView.setOnClickListener(new View.OnClickListener() {
            /**
             * This method reacts to a click and shows the map 
             * @param v
             */
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + vh.latitude + "," + vh.longitude + "&mode=d");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                context.startActivity(mapIntent);
            }
        });

        return vh;
    }

    /**
     * This method get the color for a cards background
     * @param status
     * @return the color of a card
     */
    private int getParkingColor(Parking.ParkingStatus status) {
        switch (status) {
            case PARKING_FULL:
            case PARKING_FORBIDDEN:
                return getColor(context, R.color.redParking);
            default:
                return getColor(context, R.color.greenParking);
        }
    }

    /**
     * This method get the text to display for a card
     * @param status
     * @param freeSpots
     * @return a String that describes the status of a parking
     */
    private String getText(Parking.ParkingStatus status, Integer freeSpots) {
        switch (status) {
            case PARKING_FORBIDDEN:
                return "P-förbud";
            case PARKING_FULL:
                return "0 lediga platser";
            case PARKING_ALLOWED:
                return "Ingen info om lediga platser";
            case SPOTS_AVAILABLE:
                return freeSpots + " lediga platser";
            default:
                return "";
        }
    }

    /**
     * This method get the icon to display on the screen
     * @param status
     * @return the image to display
     */
    private int getIcon(Parking.ParkingStatus status){
        int forbidden = R.drawable.no_parking;
        int noInfo = R.drawable.warning;
        if(status == Parking.ParkingStatus.PARKING_FORBIDDEN ){
            return forbidden;
        }
        else if(status == Parking.ParkingStatus.PARKING_ALLOWED){
            return noInfo;
        }
        return 0;
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     * This method should update the contents of the item views to reflect the item at the given position.
     * @param holder    The ViewHolder which should be updated to represent the contents
     *                  of the item at the given position in the data set.
     * @param position  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(ParkingViewHolder holder, int position) {
        Parking p = mDataset.get(position);

        holder.mTextViewName.setText(p.getName());
        holder.mTextViewInfo.setText(p.getExtraInformation());
        holder.mTextViewDist.setText(String.valueOf(p.getDistance()) + " m");
        holder.latitude = p.getLatitude();
        holder.longitude = p.getLongitude();
        
        holder.mSign.setImageResource(getIcon(p.getParkingStatus()));
        holder.mParkingColor.setBackgroundColor(getParkingColor(p.getParkingStatus()));
        holder.mTextViewPark.setText(getText(p.getParkingStatus(),p.getFreeSpots()));

    }

    /**
     * Return the size of the model (the number of items).
     * In our case how many parking objects there is in our data set,
     * which is also the number of items in our list.
     */
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}