package com.upperz.sharktracker.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.parse.FunctionCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.upperz.sharktracker.Activities.SharkActivity;
import com.upperz.sharktracker.MyApplication;
import com.upperz.sharktracker.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMapClickListener, GoogleMap.InfoWindowAdapter {

    public List<Marker> markerList = new ArrayList<>();
    public List<Polyline> mPolyLines = new ArrayList<>();


    public Snackbar snack;

    public FloatingActionButton fab;
    public Marker temporaryStartMarker;
    public Marker temporaryEndMarker;
    public Marker mCurrentMarkerSelected;
    public MapView mapView;
    public GoogleMap map;
    public TextView latitudeView;
    public TextView longitudeView;
    public TextView nameView;
    public TextView sponsorView;

    public SweetAlertDialog mDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        /*Inflate the fragment view for the map*/
        View v =inflater.inflate(R.layout.fragment_map,container,false);

        /*Initialize the floating action button*/
        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zoomOutAndReload();

            }
        });
        fab.setVisibility(View.INVISIBLE);

        /*Create reference to mapview and begin loading the map*/
        mapView = (MapView) v.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        /*Return the view to be loaded*/
        return v;
    }

    public void createSharkTrack(final String name)
    {

        createDialog("Loading Shark Track");
        if(temporaryStartMarker != null)
            temporaryStartMarker.remove();

        adjustUI(0);

        MyApplication.sharkTrackParams.put("shark", name);
        ParseCloud.callFunctionInBackground("createSharkTrack", MyApplication.sharkTrackParams, new FunctionCallback<ArrayList<ParseGeoPoint>>() {
            @Override
            public void done(ArrayList<ParseGeoPoint> parseObjects, ParseException e) {

                if (e == null) {

                    ArrayList<LatLng> mLatLngs = new ArrayList<>();

                    for (ParseGeoPoint geoPoint : parseObjects)
                        mLatLngs.add(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()));


                    if (mCurrentMarkerSelected == null) {
                        for (Marker m : markerList) {
                            if (m.getTitle().equals(name))
                                m.setVisible(true);
                        }
                    }

                    mPolyLines.add(map.addPolyline(new PolylineOptions().width(5).geodesic(true).color(Color.RED).addAll(mLatLngs)));
                    mDialog.dismiss();
                    MyApplication.sharkTrackParams.clear();


                }

            }
        });


        fab.setVisibility(View.VISIBLE);

        MyApplication.dimensions.clear();
        MyApplication.dimensions.put("Shark", name);
        ParseAnalytics.trackEventInBackground("Tracks", MyApplication.dimensions);


    }

    public void zoomOutAndReload()
    {


        mCurrentMarkerSelected = null;

        if (temporaryEndMarker != null)
            temporaryEndMarker.remove();
        temporaryEndMarker = null;

        if(temporaryStartMarker != null)
            temporaryStartMarker.remove();
        temporaryStartMarker = null;

        if(snack != null)
            snack.dismiss();


        for (Marker m : markerList)
            m.setVisible(true);

        for (Polyline polyline : mPolyLines) {
            polyline.remove();}
        mPolyLines.clear();

        map.animateCamera(CameraUpdateFactory.zoomBy(-1));

        fab.setVisibility(View.INVISIBLE);


    }

    public void createSnackBar()
    {

        if(mCurrentMarkerSelected != null) {
            snack = Snackbar.make(getView(), mCurrentMarkerSelected.getTitle() + " Clicked", Snackbar.LENGTH_INDEFINITE);
            snack.setAction("SEE TRACK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mCurrentMarkerSelected.hideInfoWindow();

                    createSharkTrack(mCurrentMarkerSelected.getTitle());

                }
            });
            snack.show();
        }

    }

    public void closeSnackBar()
    {
        if(snack != null)
            if(snack.isShown())
                snack.dismiss();
    }

    private void createDialog(String dialogTitle)
    {
        mDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        mDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        mDialog.setTitleText(dialogTitle);
        mDialog.setCancelable(false);
        mDialog.show();
    }


    public void adjustUI(int adjustmentType)
    {
        switch (adjustmentType)
        {
            case 0:
                if(mCurrentMarkerSelected != null)
                    mCurrentMarkerSelected.hideInfoWindow();
                closeSnackBar();
                hideNonSelectedMarkers();




        }
    }

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
    }



    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View v = View.inflate(getContext(), R.layout.custom_info_window, null);
        v.findViewById(R.id.sponsor_layout).setVisibility(View.GONE);

        nameView = (TextView) v.findViewById(R.id.name);
        latitudeView = (TextView) v.findViewById(R.id.date_info_window);
        longitudeView = (TextView) v.findViewById(R.id.species_info_window);
        sponsorView = (TextView) v.findViewById(R.id.sponsor_info_window);

        for (ParseObject aSharkInfo : MyApplication.sharks) {

            if (aSharkInfo.getString("shark").equals(marker.getTitle())) {
                nameView.setText(aSharkInfo.getString("shark"));
                latitudeView.setText(aSharkInfo.getString("date"));
                longitudeView.setText(aSharkInfo.getString("species"));

                if(Arrays.asList(getResources().getStringArray(R.array.sponsor_sharks)).contains(aSharkInfo.getString("shark")))
                {
                    v.findViewById(R.id.sponsor_layout).setVisibility(View.VISIBLE);
                    sponsorView.setText(MyApplication.sharkSponsors.get(aSharkInfo.getString("shark")));
                }
            }
        }

        return v;
    }

    @Override
    public void onInfoWindowClick(Marker marker)
    {
        Intent intent = new Intent(getActivity(), SharkActivity.class);
        intent.putExtra("name", marker.getTitle());

        startActivity(intent);

    }

    @Override
    public void onMapClick(LatLng latLng) {

        zoomOutAndReload();

    }

    /***
     *
     * @param googleMap Reference to the Google Map Currently loaded
     */
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        /*Move to map so that googleMap can be accessed from anywhere*/


        /*Set the standard listeners and default states for the google map*/
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(26.48, -68.486), 2.0f));
        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnMapClickListener(this);
        googleMap.setInfoWindowAdapter(this);
        googleMap.setOnInfoWindowClickListener(this);

        map = googleMap;

        /*Add all the animals to the map*/
        for (String animalName : MyApplication.animals.keySet()) {

            markerList.add(map.addMarker(MyApplication.animals.get(animalName).latestLocation));

        }

    }

    @Override
    public void onCreate(Bundle SavedInstanceState)
    {
        super.onCreate(SavedInstanceState);
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        mapView.onLowMemory();


    }

    @Override
    public boolean onMarkerClick(Marker marker)
    {
        marker.showInfoWindow();

        mCurrentMarkerSelected = marker;

        createSnackBar();

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 5), 2000, null);

        return true;
    }
}
