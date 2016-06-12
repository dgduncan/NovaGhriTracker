package com.upperz.sharktracker.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.example.dgduncan.myapplication.backend.myApi.MyApi;
import com.example.dgduncan.myapplication.backend.myApi.model.Animal;
import com.example.dgduncan.myapplication.backend.myApi.model.AnimalCollection;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.upperz.sharktracker.MyApplication;

import java.io.IOException;

public class SplashActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*Query GAE to get latest locations*/
        new EndpointsAsyncTask().execute();
    }

    /**
     * Async task that creates a reference to my AnimalApi, queries for the latest locations, and
     * stores this data as a global variable
     */
    class EndpointsAsyncTask extends AsyncTask<Void, Void, AnimalCollection>
    {

        /*Reference to AsyncTask used to stop task if timer goes over time*/
        private EndpointsAsyncTask endpointsAsyncTask = this;

        /*Reference to my Backend API*/
        private MyApi myApiService = null;

        /*Dialog to notify user about what is going on*/
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute()
        {

            /*Create progress dialog to notify user that application is loading*/
            progressDialog = new ProgressDialog(SplashActivity.this);
            progressDialog.setMessage("Loading animal locations ... ");
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.show();


            /*CountDownTimer to prevent the request from running for ever*/
            new CountDownTimer(15000, 15000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    //TODO : Add onTick code to tell user what is going on

                }

                @Override
                public void onFinish()
                {
                    /*Check if AsyncTask is actually still running*/
                    if(endpointsAsyncTask.getStatus() == Status.RUNNING)
                    {
                        //Cancel task if still running
                        endpointsAsyncTask.cancel(true);

                        progressDialog.dismiss();

                        //Create an alert dialog asking user to retry
                        new AlertDialog.Builder(SplashActivity.this)
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
        protected AnimalCollection doInBackground(Void ... params)
        {
            /*Build API service*/
            if(myApiService == null)
            {
                MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                        .setRootUrl("https://sharktracker-1142.appspot.com/_ah/api/");

                myApiService = builder.build();
            }

            /*Attempt to get animals from API*/
            try
            {
                return myApiService.getAllAnimals().execute();
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return null;
            }


        }

        @Override
        protected void onPostExecute(AnimalCollection result)
        {
            /*Check if result is null to catch any network error*/
            if(result != null)
            {
                //TODO : I don't like the way that this is done
                MyApplication.sharks = result.getItems();

                /*Put animal references into the MyApplication*/
                for(Animal animal : result.getItems())
                {
                    MyApplication.animals.put(animal.getName(), animal);
                }

                /*Dismiss the dialog to tell user that everything is done*/
                progressDialog.dismiss();

                /*Start the activity to go to the tabbed activity*/
                Intent intent = new Intent(SplashActivity.this, NavigationTabBar.class);
                startActivity(intent);

                /*finish() prevents the user from being able to back press back into the splash*/
                finish();
            }

            else
            {
                /*Build an Alert Dialog to tell user that a network has occured*/
                new AlertDialog.Builder(SplashActivity.this)
                        .setTitle("Connection Error")
                        .setMessage("There seems to be an issue connecting to our backend," +
                                " please check your internet connection and try again.")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        }
    }
}
