package com.upperz.sharktracker.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.MapsInitializer;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.upperz.sharktracker.Classes.Animal;
import com.upperz.sharktracker.MyApplication;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SplashActivity extends AppCompatActivity
{
    /*Tag for use in logging*/
    String TAG = getClass().getSimpleName();

    /*Dialog to notify user that app is loading*/
    SweetAlertDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*Must initialize this first in order to create marker options*/
        MapsInitializer.initialize(getApplicationContext());

        /*Query Parse to get latest locations*/
        updateLocally();
    }

    /***
     * Function used to query parse for the latest locations and to create animal objects
     * from the returned query data.
     */
    private void updateLocally()
    {
        showDialog("Updating Animal Locations");

        if(MyApplication.sharks.size() == 0)
        {
            ParseCloud.callFunctionInBackground("getLatestLocations", MyApplication.params, new FunctionCallback<ArrayList<ParseObject>>() {
                @Override
                public void done(final ArrayList<ParseObject> p, ParseException e) {
                    if (e == null) {
                        MyApplication.sharks.addAll(p);

                        createAnimalReferences(p);


                        pDialog.dismissWithAnimation();

                        Intent intent = new Intent(SplashActivity.this, MainTabbedActivity.class);
                        startActivity(intent);
                        finish();
                    } else
                        Log.d(TAG, e.toString());

                }

            });
        }

        else
        {
            Intent intent = new Intent(SplashActivity.this, MainTabbedActivity.class);
            startActivity(intent);
            finish();
        }





    }

    //TODO : Search if there is another dialog option that will not block logo. Is this even necessary?
    private void showDialog(String dialogTitle)
    {
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(dialogTitle);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    /***
     * Function used to create all of the animal objects used
     * @param latestLocationsOfAnimals the list of parseobjects returned from query to be made into
     *                                 animal objects
     */
    private void createAnimalReferences(ArrayList<ParseObject> latestLocationsOfAnimals)
    {
        for(ParseObject newShark : latestLocationsOfAnimals)
        {
            MyApplication.animals.put(newShark.getString("shark"), new Animal(newShark));
        }

    }
}
