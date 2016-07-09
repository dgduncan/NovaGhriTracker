package com.upperz.sharktracker.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cocosw.bottomsheet.BottomSheet;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.upperz.sharktracker.Activities.SharkActivity;
import com.upperz.sharktracker.Classes.Animal;
import com.upperz.sharktracker.Classes.MapHelper;
import com.upperz.sharktracker.MyApplication;
import com.upperz.sharktracker.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import cz.msebera.android.httpclient.Header;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMapClickListener, GoogleMap.InfoWindowAdapter, GoogleMap.OnMapLoadedCallback {

    public MapHelper mapHelper;

    public MapView mapView;

    public ProgressDialog progressDialog;

    public TextView commonView;
    public TextView latitudeView;
    public TextView longitudeView;
    public TextView nameView;
    public TextView sponsorView;

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

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading Track Information ... ");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();

        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams requestParams = new RequestParams();
        requestParams.add("name", name);

        client.get("http://104.197.207.240:8080/api/getSpecificTrack", requestParams, new JsonHttpResponseHandler() {

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject jsonObject)
            {

                progressDialog.dismiss();


                Log.e("SplashActivity", String.valueOf(statusCode));

                new AlertDialog.Builder(getActivity())
                        .setTitle("Connection Error")
                        .setMessage("There seems to be an issue connecting to our backend," +
                                " we apologize for the issue and please try again later.")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().finish();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // If the response is JSONObject instead of expected JSONArray

                progressDialog.dismiss();


            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray timeline) {

                progressDialog.dismiss();

                ArrayList<LatLng> latLngArrayList = new ArrayList<>();

                try
                {
                    String name = timeline.getJSONObject(1).getString("name");

                    for (int index = 0; index < timeline.length(); index++)
                    {
                        latLngArrayList.add(new LatLng(timeline.getJSONObject(index).getDouble("latitude"), timeline.getJSONObject(index).getDouble("longitude")));
                    }

                    mapHelper.createSharkTrack(name, latLngArrayList);

                }
                catch (JSONException e) {
                    e.printStackTrace();
                }







            }
        });


    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View v = View.inflate(getContext(), R.layout.custom_info_window, null);
        v.findViewById(R.id.sponsor_layout).setVisibility(View.GONE);

        commonView = (TextView) v.findViewById(R.id.common_name_info_window);
        nameView = (TextView) v.findViewById(R.id.name);
        latitudeView = (TextView) v.findViewById(R.id.date_info_window);
        longitudeView = (TextView) v.findViewById(R.id.species_info_window);
        sponsorView = (TextView) v.findViewById(R.id.sponsor_info_window);


        for (Animal animal : MyApplication.sharks) {

            if (animal.name.equals(marker.getTitle()))
            {
                commonView.setText(animal.common_name);
                nameView.setText(animal.name);
                latitudeView.setText(animal.date);
                longitudeView.setText(animal.species);

                if(Arrays.asList(getResources().getStringArray(R.array.sponsor_sharks)).contains(animal.name))
                {
                    v.findViewById(R.id.sponsor_layout).setVisibility(View.VISIBLE);
                    sponsorView.setText(MyApplication.sharkSponsors.get(animal.name));
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
                                createSharkTrack(marker.getTitle());
                                break;


                        }
                    }
                }).show();


    }

    @Override
    public void onMapClick(LatLng latLng) {
        mapHelper.updateUI(0, 0, null);
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
     *//*
    class EndpointsAsyncTask extends AsyncTask<String, Void, TrackCollection>
    {
        private String name;

        *//*Reference to AsyncTask used to stop task if timer goes over time*//*
        private EndpointsAsyncTask endpointsAsyncTask = this;

        *//*Reference to my Backend API*//*
        private TrackApi myApiService = null;

        *//*Dialog to notify user about what is going on*//*
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
                    *//*Check if AsyncTask is actually still runnning*//*
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
            *//*Build API service*//*
            if(myApiService == null)
            {
                TrackApi.Builder builder = new TrackApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                        .setRootUrl("https://sharktracker-1142.appspot.com/_ah/api/");

                myApiService = builder.build();
            }

            name = params[0];


            *//*Attempt to get animals from API*//*
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
            *//*Create an Array List of Latitudes and Longitudes*//*
            ArrayList<LatLng> mLatLngs = new ArrayList<>();

            *//*Create add all the latitudes and longitudes*//*
            for (Track track : result.getItems())
                mLatLngs.add(new LatLng(track.getLocation().getLatitude(), track.getLocation().getLongitude()));

            *//*Create the shark track using the mapHelper*//*
            mapHelper.createSharkTrack(name, mLatLngs);

            *//*Dismiss dialog*//*
            progressDialog.dismiss();



        }
    }*/
}
