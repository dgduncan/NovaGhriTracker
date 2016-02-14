package com.upperz.sharktracker.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.upperz.sharktracker.MyApplication;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SplashActivity extends AppCompatActivity {



    String TAG = getClass().getSimpleName();

    SweetAlertDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getAnimalNames();
        updateLocally();
    }

    private void getAnimalNames()
    {
        ParseQuery<ParseObject> startQuery = ParseQuery.getQuery("Latestlocations");
        startQuery.setLimit(200);
        startQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
                    Log.d(TAG, String.valueOf(scoreList.size()));

                }
            }
        });
    }

    private void updateLocally()
    {
        Log.i(TAG, "Attempting to update information locally");
        showDialog("Updating Animal Locations");

        ParseCloud.callFunctionInBackground("getLatestLocations", MyApplication.params, new FunctionCallback<ArrayList<ParseObject>>() {
            @Override
            public void done(final ArrayList<ParseObject> p, ParseException e) {

                if (e == null) {
                    MyApplication.sharks.addAll(p);

                    pDialog.dismissWithAnimation();

                    Intent intent = new Intent(SplashActivity.this, MainTabbedActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
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
}
