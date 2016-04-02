package com.upperz.sharktracker.Classes;

import android.graphics.Color;

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

    /*Map States*/
    public boolean trackShown;

    private GoogleMap googleMap;

    private Marker currentMarkerSelected;
    private Marker temporaryEndMarker;
    private Marker temporaryStartMarker;

    /**
     * Empty Constructor
     */
    public MapHelper(){}

    public void buildMarker(Animal animal, boolean initialLocation)
    {
        if(!initialLocation)
        {
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

    public void createSharkTrack(String name, ArrayList<LatLng> mLatLngs)
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

        /*Create an initial map marker for the animal whose track is shown*/
        buildMarker(MyApplication.animals.get(name), true);

        /*Add polyLines to the array list*/
        polyLines.add(googleMap.addPolyline(new PolylineOptions().width(5).geodesic(true).color(Color.RED).addAll(mLatLngs)));


    }

    public void createSharkTrackFromTabber(String name)
    {
        trackShown = true;

        if(temporaryStartMarker != null)
            temporaryStartMarker.remove();

        if(currentMarkerSelected != null)
            currentMarkerSelected.remove();

        for(Polyline p : polyLines)
        {
            p.remove();
        }


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

        buildMarker(MyApplication.animals.get(name), true);
    }

    /*
    public void hideNonSelectedMarkers()
    {
        Marker mCurrentMarker = mCurrentMarkerSelected;

        if(mCurrentMarker != null) {
            for (Marker m : markerList) {
                {
                    if (!(m.equals(mCurrentMarkerSelected)))
                        m.setVisible(false);
                }
            }
        }

        else
        {
            for (Marker m : markerList) {
                {
                    m.setVisible(false);
                }
            }
        }
    }*/

    public void initialize(GoogleMap googleMap)
    {
        this.googleMap = googleMap;
    }


    public void onMarkerClick(Marker marker)
    {
        if(!trackShown)
        {
            marker.showInfoWindow();

            currentMarkerSelected = marker;


            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 5), 2000, null);

        }
    }

    public void resetView()
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


}
