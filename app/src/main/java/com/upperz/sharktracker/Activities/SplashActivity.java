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

public class SplashActivity extends AppCompatActivity {



    String TAG = getClass().getSimpleName();

    SweetAlertDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MapsInitializer.initialize(getApplicationContext());

        updateLocally();
    }

    private void updateLocally()
    {
        showDialog("Updating Animal Locations");

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

    private void showDialog(String dialogTitle)
    {
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText(dialogTitle);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private void createAnimalReferences(ArrayList<ParseObject> latestLocationsOfAnimals)
    {
        for(ParseObject newShark : latestLocationsOfAnimals)
        {
            MyApplication.animals.put(newShark.getString("shark"), new Animal(newShark));
        }

    }
}
