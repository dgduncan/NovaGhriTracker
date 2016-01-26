package com.upperz.sharktracker;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SplashActivity extends AppCompatActivity {

    private SweetAlertDialog pDialog;

    private String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateLocally();
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

    /*private void initialQuery() {

        Log.i(TAG, "initialQuery Entered");

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();

        ParseCloud.callFunctionInBackground("getLatestLocations", MyApplication.params, new FunctionCallback<ArrayList<ParseObject>>() {
            @Override
            public void done(final ArrayList<ParseObject> p, ParseException e) {

                if (e == null) {
                    MyApplication.sharks.addAll(p);

                    ParseObject.unpinAllInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                ParseObject.pinAllInBackground(p, new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            Log.i(TAG, "Latestlocations pinned in background");

                                            pDialog.dismiss();

                                            SharedPreferences.Editor editor = sharedpreferences.edit();
                                            editor.putInt("firstTimeFlag", 1);
                                            editor.apply();



                                            Intent intent = new Intent(SplashActivity.this, MainTabbedActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Log.d(TAG, e.toString());
                                        }
                                    }
                                });
                            } else
                                Log.d(TAG, e.toString());
                        }
                    });
                } else {
                    Log.d(TAG, e.toString());

                    if(e.getCode() == 100)
                    {
                        Log.d(TAG, "No Internet Available");

                        pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        pDialog.setTitleText("No Internet Connection Available");
                        pDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {

                                if(Build.VERSION.SDK_INT > 16)
                                    finishAffinity();
                                else
                                {
                                    finish();
                                    System.exit(0);
                                }

                            }
                        });
                    }
                }

            }

        });
    }

    private void returningQuery()
    {

        Log.i(TAG, "returningQuery Entered");

        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();

        ParseCloud.callFunctionInBackground("getLatestLocations", MyApplication.params, new FunctionCallback<ArrayList<ParseObject>>() {
            @Override
            public void done(final ArrayList<ParseObject> p, ParseException e) {
                if(e==null)
                {
                    MyApplication.sharks.addAll(p);

                    ParseObject.unpinAllInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                ParseObject.pinAllInBackground(p, new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            Log.i(TAG, "Latestlocations pinned and updated in background");

                                            pDialog.dismissWithAnimation();

                                            Intent intent = new Intent(SplashActivity.this, MainTabbedActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Log.d(TAG, e.toString());
                                        }
                                    }
                                });
                            } else
                                Log.d(TAG, e.toString());
                        }
                    });
                }
                else
                    Log.d(TAG, e.toString());
            }
        });

    }*/


}
