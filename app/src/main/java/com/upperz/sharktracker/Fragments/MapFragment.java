package com.upperz.sharktracker.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cocosw.bottomsheet.BottomSheet;
import com.example.dgduncan.myapplication.backend.models.trackApi.TrackApi;
import com.example.dgduncan.myapplication.backend.models.trackApi.model.Track;
import com.example.dgduncan.myapplication.backend.models.trackApi.model.TrackCollection;
import com.example.dgduncan.myapplication.backend.myApi.model.Animal;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.upperz.sharktracker.Activities.SharkActivity;
import com.upperz.sharktracker.Classes.MapHelper;
import com.upperz.sharktracker.MyApplication;
import com.upperz.sharktracker.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMapClickListener, GoogleMap.InfoWindowAdapter, GoogleMap.OnMapLoadedCallback {

    public MapView mapView;
    public TextView latitudeView;
    public TextView longitudeView;
    public TextView nameView;
    public TextView sponsorView;

    public ProgressDialog progressDialog;

    public MapHelper mapHelper;




    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        /*Inflate the fragment view for the map*/
        View v =inflater.inflate(R.layout.fragment_map,container,false);

        /*Create reference to mapView and begin loading the map*/
        mapView = (MapView) v.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        /*Create progress dialog*/
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

        new EndpointsAsyncTask().execute(name);


    }

    public void createTrackFromTabber(final String name)
    {
        new EndpointsAsyncTask().execute(name);

        mapHelper.createSharkTrackFromTabber(name);



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
    public void onInfoWindowClick(final Marker marker)
    {
        new BottomSheet.Builder(getContext(), R.style.BottomSheet_Dialog)
                .title("Extended Options")
                .sheet(R.menu.menu_bottom_sheet_map)
                .listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which)
                        {
                            case R.id.profile:
                                Intent intent = new Intent(getActivity(), SharkActivity.class);
                                intent.putExtra("name", marker.getTitle());
                                startActivityForResult(intent, 1);
                                break;

                            case R.id.track:
                                new EndpointsAsyncTask().execute(marker.getTitle());
                                break;


                        }
                    }
                }).show();


    }

    @Override
    public void onMapClick(LatLng latLng) {
        mapHelper.resetView();
    }

    @Override
    public void onMapLoaded() {

        for(Animal animal : MyApplication.sharks)
        {
            mapHelper.buildMarker(animal, false);

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


        mapHelper = new MapHelper();
        mapHelper.initialize(googleMap);


    }

    @Override
    public boolean onMarkerClick(Marker marker)
    {
        mapHelper.onMarkerClick(marker);

        return true;
    }


    //**********************************************************************************************
    /*Activity Class Overrides*/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                createSharkTrack(data.getStringExtra("trackName"));
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

    //**********************************************************************************************

    /***
     * Async task that creates a reference to my AnimalApi, queries for the latest locations, and
     * stores this data as a global variable
     */
    class EndpointsAsyncTask extends AsyncTask<String, Void, TrackCollection>
    {
        private String name;

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

            name = params[0];


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

            mapHelper.createSharkTrack(name, mLatLngs);

            progressDialog.dismiss();



        }
    }
}
