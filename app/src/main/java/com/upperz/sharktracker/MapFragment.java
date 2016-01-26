package com.upperz.sharktracker;

import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MapFragment extends Fragment implements GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMapClickListener, GoogleMap.InfoWindowAdapter {

    public DateTimeFormatter dateTimeFormat;
    public DateTime dateTime;


    public List<Marker> markerList = new ArrayList<>();
    public List<Polyline> mPolyLines = new ArrayList<>();


    public String TAG = "MapFragment";
    public Snackbar snack;

    public FloatingActionButton fab;
    public Marker temporaryStartMarker;
    public Marker temporaryEndMarker;
    public MapView mapView;
    public GoogleMap map;
    public TextView latitudeView;
    public TextView longitudeView;
    public TextView nameView;
    public TextView sponsorView;

    public SweetAlertDialog mDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v =inflater.inflate(R.layout.fragment_map,container,false);

        createDialog("Loading Map");

        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zoomOutAndReload();

            }
        });
        fab.setVisibility(View.INVISIBLE);

        mapView = (MapView) v.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        map = mapView.getMap();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(26.48, -68.486), 2.0f));
        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        map.setOnMarkerClickListener(this);
        map.setOnMapClickListener(this);
        map.setInfoWindowAdapter(this);
        map.setOnInfoWindowClickListener(this);

        loadCurrentLocations();

        return v;
    }

    public void loadCurrentLocations()
    {
        for (ParseObject x : MyApplication.sharks) {
            SetMapMarker(x);
            mDialog.dismissWithAnimation();
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
    public void onInfoWindowClick(Marker marker) {

        MyApplication.mCurrentSharkSelected = marker.getTitle();

        for(ParseObject x : MyApplication.sharks)
        {
            if(x.getString("shark").equalsIgnoreCase(marker.getTitle()))
                MyApplication.mCurrentObjectParseObject = x;
        }

        adjustScreenPosition(1);
    }

    @Override
    public void onMapClick(LatLng latLng) {

        zoomOutAndReload();
        if(snack != null)
            snack.dismiss();

    }


    @Override
    public boolean onMarkerClick(final Marker marker) {

        MyApplication.mCurrentMarkerSelected = marker;

        if(temporaryStartMarker == null)
            createSnackBar();

        marker.showInfoWindow();

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 5), 2000, null);

        MyApplication.dimensions.clear();
        MyApplication.dimensions.put("Shark", marker.getTitle());
        ParseAnalytics.trackEventInBackground("MarkerClick", MyApplication.dimensions);



        return true;
    }



    public void SetMapMarker(ParseObject sharkObject)
    {
        if(checkIfRecent(sharkObject))
        {
            MarkerOptions marker = new MarkerOptions().position(new LatLng(Double.valueOf(sharkObject.getString("latitude")), Double.valueOf(sharkObject.getString("longitude")))).title(sharkObject.getString("shark")).icon(BitmapDescriptorFactory.fromResource(R.drawable.green_measle));
            markerList.add(map.addMarker(marker));
        }
        else
        {
            MarkerOptions marker = new MarkerOptions().position(new LatLng(Double.valueOf(sharkObject.getString("latitude")), Double.valueOf(sharkObject.getString("longitude")))).title(sharkObject.getString("shark")).icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_measle));
            markerList.add(map.addMarker(marker));
        }
    }

    public void setStartMarker(final String name)
    {
        ParseQuery<ParseObject> startQuery = ParseQuery.getQuery("Animals");
        startQuery.whereEqualTo("shark", name);
        startQuery.whereEqualTo("sequence", "1");
        startQuery.setLimit(1);
        startQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null)
                {
                    MarkerOptions marker = new MarkerOptions().position(new LatLng(Double.valueOf(scoreList.get(0).getString("latitude")), Double.valueOf(scoreList.get(0).getString("longitude"))))
                            .title(name).icon(BitmapDescriptorFactory.fromResource(R.drawable.star_marker));
                    temporaryStartMarker = map.addMarker(marker);

                }
            }
        });
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


                    if(MyApplication.mCurrentMarkerSelected == null)
                    {
                        for(Marker m : markerList)
                        {
                            if(m.getTitle().equals(name))
                                m.setVisible(true);
                        }
                    }



                    setStartMarker(name);
                    mPolyLines.add(map.addPolyline(new PolylineOptions().width(5).geodesic(true).color(Color.RED).addAll(mLatLngs)));
                    mDialog.dismiss();
                    MyApplication.sharkTrackParams.clear();


                }

            }
        });


        /*if(MyApplication.mCurrentMarkerSelected != null) {


            MyApplication.sharkTrackParams.put("shark", name);

            ParseCloud.callFunctionInBackground("createSharkTrack", MyApplication.sharkTrackParams, new FunctionCallback<ArrayList<ParseGeoPoint>>() {
                @Override
                public void done(ArrayList<ParseGeoPoint> parseObjects, ParseException e) {

                    if (e == null) {

                        ArrayList<LatLng> latLngs = new ArrayList<>();

                        for (ParseGeoPoint geoPoint : parseObjects)
                            latLngs.add(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()));

                        MarkerOptions marker = new MarkerOptions().position(new LatLng(parseObjects.get(0).getLatitude(), parseObjects.get(0).getLongitude()))
                                .title(MyApplication.mCurrentSharkSelected).icon(BitmapDescriptorFactory.fromResource(R.drawable.star_marker));
                        temporaryStartMarker = map.addMarker(marker);

                        mPolyLines.add(map.addPolyline(new PolylineOptions().width(5).geodesic(true).color(Color.RED).addAll(latLngs)));

                        mDialog.dismiss();

                        MyApplication.sharkTrackParams.clear();


                    }

                }
            });
        }
        else
        {



            MyApplication.sharkTrackParams.put("shark", name);

            ParseCloud.callFunctionInBackground("createSharkTrack", MyApplication.sharkTrackParams, new FunctionCallback<ArrayList<ParseGeoPoint>>() {
                @Override
                public void done(ArrayList<ParseGeoPoint> parseObjects, ParseException e) {

                    if (e == null) {

                        ArrayList<LatLng> latLngs = new ArrayList<>();

                        MarkerOptions marker = new MarkerOptions().position(new LatLng(parseObjects.get(0).getLatitude(), parseObjects.get(0).getLongitude()))
                                .title(MyApplication.mCurrentSharkSelected).icon(BitmapDescriptorFactory.fromResource(R.drawable.star_marker));
                        temporaryStartMarker = map.addMarker(marker);

                        marker = new MarkerOptions().position(new LatLng(parseObjects.get(parseObjects.size()-1).getLatitude(), parseObjects.get(parseObjects.size()-1).getLongitude()))
                                .title(MyApplication.mCurrentSharkSelected).icon(BitmapDescriptorFactory.fromResource(R.drawable.green_measle));
                        temporaryEndMarker = map.addMarker(marker);

                        for (ParseGeoPoint geoPoint : parseObjects)
                            latLngs.add(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()));

                        mPolyLines.add(map.addPolyline(new PolylineOptions().width(5).geodesic(true).color(Color.RED).addAll(latLngs)));

                        mDialog.dismiss();

                        MyApplication.sharkTrackParams.clear();


                    }

                }
            });

        }*/

        fab.setVisibility(View.VISIBLE);

        MyApplication.dimensions.clear();
        MyApplication.dimensions.put("Shark", name);
        ParseAnalytics.trackEventInBackground("Tracks", MyApplication.dimensions);


    }

    public void zoomOutAndReload()
    {


        MyApplication.mCurrentMarkerSelected = null;
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

        if(MyApplication.mCurrentMarkerSelected != null) {
            snack = Snackbar.make(getView(), MyApplication.mCurrentMarkerSelected.getTitle() + " Clicked", Snackbar.LENGTH_INDEFINITE);
            snack.setAction("SEE TRACK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    MyApplication.mCurrentMarkerSelected.hideInfoWindow();
                    createSharkTrack(MyApplication.mCurrentMarkerSelected.getTitle());

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

    public boolean checkIfRecent(ParseObject objectToCheckIfRecent) {

        dateTimeFormat = DateTimeFormat.forPattern("MM/dd/yyyy");
        dateTime = dateTimeFormat.parseDateTime(objectToCheckIfRecent.getString("date"));

        return Days.daysBetween(dateTime, new LocalDateTime().toDateTime()).getDays() <= 14;

    }

    public void adjustScreenPosition(int screenNumber)
    {

        ViewPager vp = (ViewPager) getActivity().findViewById(R.id.container);
        TabLayout tl = (TabLayout) getActivity().findViewById(R.id.tabs);
        vp.setCurrentItem(screenNumber);
        tl.setScrollPosition(screenNumber, 0, true);

    }

    public void adjustUI(int adjustmentType)
    {
        switch (adjustmentType)
        {
            case 0:
                if(MyApplication.mCurrentMarkerSelected != null)
                    MyApplication.mCurrentMarkerSelected.hideInfoWindow();
                closeSnackBar();
                hideNonSelectedMarkers();




        }
    }

    public void hideNonSelectedMarkers()
    {
        Marker mCurrentMarker = MyApplication.mCurrentMarkerSelected;

        if(mCurrentMarker != null) {
            for (Marker m : markerList) {
                {
                    if (!(m.equals(MyApplication.mCurrentMarkerSelected)))
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
    public void onCreate(Bundle SavedInstanceState)
    {
        super.onCreate(SavedInstanceState);
    }


    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();

        Log.d(TAG, "onResume Called");
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();

        MyApplication.mCurrentMarkerSelected = null;

        Log.d(TAG, "onPause Called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();

        Log.d(TAG, "onDestroy Called");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();

        Log.d(TAG, "onLowMemory Called");
    }

}
