package com.upperz.sharktracker.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dgduncan.myapplication.backend.models.trackApi.TrackApi;
import com.example.dgduncan.myapplication.backend.models.trackApi.model.Track;
import com.example.dgduncan.myapplication.backend.models.trackApi.model.TrackCollection;
import com.example.dgduncan.myapplication.backend.myApi.model.Animal;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.upperz.sharktracker.Activities.SharkActivity;
import com.upperz.sharktracker.MyApplication;
import com.upperz.sharktracker.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMapClickListener, GoogleMap.InfoWindowAdapter, GoogleMap.OnMapLoadedCallback {

    public List<Marker> markerList = new ArrayList<>();
    public List<Polyline> mPolyLines = new ArrayList<>();


    public Snackbar snack;

    public boolean trackShown;
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

    public ProgressDialog progressDialog;


    @Override
    public void onCreate(Bundle SavedInstanceState)
    {
        super.onCreate(SavedInstanceState);

    }

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

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading Map");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        /*Return the view to be loaded*/
        return v;
    }

    public void createSharkTrack(final String name)
    {

        trackShown = true;

        if(temporaryStartMarker != null)
            temporaryStartMarker.remove();

        adjustUI(0);

        createInitialTaggingLocation(name);

        new EndpointsAsyncTask().execute(name);


        fab.setVisibility(View.VISIBLE);


    }

    public void createTrackFromTabber(final String name)
    {
        trackShown = true;

        if(temporaryStartMarker != null)
            temporaryStartMarker.remove();

        if(mCurrentMarkerSelected != null)
            mCurrentMarkerSelected.remove();

        for(Polyline p : mPolyLines)
        {
            p.remove();
        }

        adjustUI(0);


        for(Marker m : markerList)
        {
            if(m.getTitle().equals(name))
            {
                m.setVisible(true);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(m.getPosition(), 5), 2000, null);
            }
            else
            {
                m.setVisible(false);
            }
        }

        createInitialTaggingLocation(name);


        /*
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
                            {
                                m.setVisible(true);
                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(m.getPosition(), 5), 2000, null);
                            }
                        }
                    }

                    mPolyLines.add(map.addPolyline(new PolylineOptions().width(5).geodesic(true).color(Color.RED).addAll(mLatLngs)));
                    mDialog.dismiss();
                    MyApplication.sharkTrackParams.clear();


                }

            }
        });*/

        new EndpointsAsyncTask().execute(name);


    }

    public void createInitialTaggingLocation(String name)
    {

        MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(Double.valueOf(MyApplication.animals.get(name).getInitialLocation().getLatitude()), Double.valueOf(MyApplication.animals.get(name).getInitialLocation().getLongitude())))
                .title(MyApplication.animals.get(name).getName())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.star_marker));
        temporaryStartMarker = map.addMarker(markerOptions);

    }

    public void zoomOutAndReload()
    {


        trackShown = false;

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
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                closeSnackBar();
                createSharkTrack(data.getStringExtra("trackName"));
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


        for (Animal animal : MyApplication.sharks) {

            if (animal.getName().equals(marker.getTitle()))
            {
                nameView.setText(animal.getName());
                latitudeView.setText(animal.getDate());
                longitudeView.setText(animal.getSpecies());

                if(Arrays.asList(getResources().getStringArray(R.array.sponsor_sharks)).contains(animal.getName()))
                {
                    v.findViewById(R.id.sponsor_layout).setVisibility(View.VISIBLE);
                    sponsorView.setText(MyApplication.sharkSponsors.get(animal.getName()));
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

        startActivityForResult(intent, 1);

    }

    @Override
    public void onMapClick(LatLng latLng) {


        zoomOutAndReload();

    }

    @Override
    public void onMapLoaded() {

        for(Animal animal : MyApplication.sharks)
        {
            if(animal.getRecent())
            {
                markerList.add(map.addMarker(new MarkerOptions()
                        .position(new LatLng(animal.getCurrentLocation().getLatitude(), animal.getCurrentLocation().getLongitude()))
                        .title(animal.getName())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.green_measle))));
            }
            else
            {
                markerList.add(map.addMarker(new MarkerOptions()
                        .position(new LatLng(animal.getCurrentLocation().getLatitude(), animal.getCurrentLocation().getLongitude()))
                        .title(animal.getName())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_measle))));
            }
        }

        progressDialog.dismiss();

    }

    /***
     *
     * @param googleMap Reference to the Google Map Currently loaded
     */
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        /*Set the standard listeners and default states for the google map*/
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(26.48, -68.486), 2.0f));
        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnMapClickListener(this);
        googleMap.setOnMapLoadedCallback(this);
        googleMap.setInfoWindowAdapter(this);
        googleMap.setOnInfoWindowClickListener(this);

        map = googleMap;


    }

    @Override
    public boolean onMarkerClick(Marker marker)
    {
        if(!trackShown)
        {
            marker.showInfoWindow();

            mCurrentMarkerSelected = marker;

            createSnackBar();

            map.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 5), 2000, null);

        }


        return true;
    }

    /***
     * Async task that creates a reference to my AnimalApi, queries for the latest locations, and
     * stores this data as a global variable
     */
    class EndpointsAsyncTask extends AsyncTask<String, Void, TrackCollection>
    {

        /*Reference to AsyncTask used to stop task if timer goes over time*/
        private EndpointsAsyncTask endpointsAsyncTask = this;

        /*Reference to my Backend API*/
        private TrackApi myApiService = null;

        /*Dialog to notify user about what is going on*/
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute()
        {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Loading Track Information ... ");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();


            new CountDownTimer(15000, 15000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    //Empty on purpose

                }

                @Override
                public void onFinish()
                {
                    /*Check if AsyncTask is actually still runnning*/
                    if(endpointsAsyncTask.getStatus() == Status.RUNNING)
                    {
                        //Cancel task if still running
                        endpointsAsyncTask.cancel(true);

                        progressDialog.dismiss();

                        //Create an alert dialog asking user to retry
                        new AlertDialog.Builder(getContext())
                                .setTitle("Connection Error")
                                .setMessage("There seems to be an issue connecting to our backend," +
                                        " would you like to retry?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        new EndpointsAsyncTask().execute();
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();

                    }

                }
            }.start();

            super.onPreExecute();

        }

        @Override
        protected TrackCollection doInBackground(String... params)
        {
            /*Build API service*/
            if(myApiService == null)
            {
                TrackApi.Builder builder = new TrackApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                        .setRootUrl("https://sharktracker-1142.appspot.com/_ah/api/");

                myApiService = builder.build();
            }


            /*Attempt to get animals from API*/
            try
            {
                return myApiService.getTrack(params[0]).execute();
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return null;
            }


        }

        @Override
        protected void onPostExecute(TrackCollection result)
        {
            ArrayList<LatLng> mLatLngs = new ArrayList<>();

            for (Track track : result.getItems())
                mLatLngs.add(new LatLng(track.getLocation().getLatitude(), track.getLocation().getLongitude()));


                    /*
                    if (mCurrentMarkerSelected == null) {
                        for (Marker m : markerList) {
                            if (m.getTitle().equals(name))
                            {
                                m.setVisible(true);
                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(m.getPosition(), 5), 2000, null);
                            }
                        }
                    }*/

            mPolyLines.add(map.addPolyline(new PolylineOptions().width(5).geodesic(true).color(Color.RED).addAll(mLatLngs)));
            progressDialog.dismiss();



        }
    }
}
