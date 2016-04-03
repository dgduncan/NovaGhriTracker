package com.upperz.sharktracker.Classes;

import android.graphics.Color;
import android.support.annotation.Nullable;

import com.example.dgduncan.myapplication.backend.myApi.model.Animal;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.upperz.sharktracker.MyApplication;
import com.upperz.sharktracker.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Written by Derek Duncan
 * April 1st, 2016
 */
public class MapHelper
{
    private List<Marker> markerList = new ArrayList<>();
    private List<Polyline> polyLines = new ArrayList<>();

    public boolean trackShown;

    private GoogleMap googleMap;

    private Marker currentMarkerSelected;
    private Marker temporaryEndMarker;
    private Marker temporaryStartMarker;

    /**
     * Empty Constructor
     */
    public MapHelper(){}

    /**
     * Creates a marker on the GoogleMap dependent upon the animal passed to it and
     * whether or not the marker is an initial tagging location marker or a current
     * location marker
     *
     * @param animal the animal object that contains the data for the marker
     * @param initialLocation boolean value that describes if the marker is a current location
     *                        marker or a initial location marker.
     */
    public void buildMarker(Animal animal, boolean initialLocation)
    {
        /*boolean flag check to see what type of marker we want*/
        if(!initialLocation)
        {
            /*recent check to choose the correct marker type. I.e green measle or blue measle*/
            if(animal.getRecent())
            {
                markerList.add(googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(animal.getCurrentLocation().getLatitude(), animal.getCurrentLocation().getLongitude()))
                        .title(animal.getName())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.green_measle))));
            }
            else
            {
                markerList.add(googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(animal.getCurrentLocation().getLatitude(), animal.getCurrentLocation().getLongitude()))
                        .title(animal.getName())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_measle))));
            }
        }

        else
        {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(new LatLng(Double.valueOf(animal.getInitialLocation().getLatitude()), Double.valueOf(animal.getInitialLocation().getLongitude())))
                    .title(animal.getName())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.star_marker));
            temporaryStartMarker = googleMap.addMarker(markerOptions);
        }


    }

    /**
     * creates the shark track of the animal with a given name and array of locations
     * @param name the name of the animal that was clicked
     * @param mLatLngs an array that contains the data to be mapped
     */
    public void createSharkTrack(String name, ArrayList<LatLng> mLatLngs)
    {
        updateUI(1, 2, name);

        /*Create an initial map marker for the animal whose track is shown*/
        buildMarker(MyApplication.animals.get(name), true);

        /*Add polyLines to the array list*/
        polyLines.add(googleMap.addPolyline(new PolylineOptions().width(5).geodesic(true).color(Color.RED).addAll(mLatLngs)));


    }

    /**
     * initializes the google map to be used with <ap Helper
     * @param googleMap reference to google map
     */
    public void initialize(GoogleMap googleMap)
    {
        this.googleMap = googleMap;
    }


    /**
     * an extension of onMarkerClick from MapFragment
     * @param marker the marker that was clicked
     */
    public void onMarkerClick(Marker marker)
    {
        if(!trackShown)
        {
            marker.showInfoWindow();

            currentMarkerSelected = marker;

            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 5), 2000, null);

        }
    }

    /**
     * updates the state of the ui
     * @param mapStartState the state at which the transition starts
     * @param mapEndState the state at which the transition ends
     * @param name name of the animal, nullable
     */
    public void updateUI(int mapStartState, int mapEndState, @Nullable String name)
    {
        //TODO : This is disgusting, there MUST be a better way
        if(mapStartState == mapEndState)
        {
            trackShown = false;

            currentMarkerSelected = null;

            if (temporaryEndMarker != null)
                temporaryEndMarker.remove();
            temporaryEndMarker = null;

            if(temporaryStartMarker != null)
                temporaryStartMarker.remove();
            temporaryStartMarker = null;


            for (Marker m : markerList)
                m.setVisible(true);

            for (Polyline polyline : polyLines) {
                polyline.remove();}
            polyLines.clear();

            googleMap.animateCamera(CameraUpdateFactory.zoomBy(-1));
        }

        if ((mapStartState == 1) && (mapEndState == 2))
        {
            /*Set flag to true to stop onClick for shown marker*/
            trackShown = true;

            /*If there is a marker currently shown from previous track, remove it*/
            if(temporaryStartMarker != null)
                temporaryStartMarker.remove();
            if(currentMarkerSelected!= null)
            {
                if(currentMarkerSelected.isInfoWindowShown())
                    currentMarkerSelected.hideInfoWindow();
            }
            //*********************************************************************

            /*Remove all polyLines*/
            for(Polyline p : polyLines)
            {
                p.remove();
            }

            /*Hide all markers except for the one currently selected*/
            for(Marker m : markerList)
            {
                if(m.getTitle().equals(name))
                {
                    m.setVisible(true);
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(m.getPosition(), 5), 2000, null);
                }
                else
                {
                    m.setVisible(false);
                }
            }
        }
    }
}
