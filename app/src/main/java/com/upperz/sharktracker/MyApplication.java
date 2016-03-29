package com.upperz.sharktracker;

import android.app.Application;

import com.example.dgduncan.myapplication.backend.myApi.model.Animal;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import net.danlew.android.joda.JodaTimeAndroid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MyApplication extends Application
{

    public static HashMap<String, Animal> animals = new HashMap<>();
    public static List<Animal> sharks = new ArrayList<>();
    public final static Map<String, String> sharkSponsors = new HashMap<>();

    public static String mCurrentSharkSelected;

    private Tracker mTracker;



    public void onCreate()
    {
        super.onCreate();

        /*Initiate Joda Time*/
        JodaTimeAndroid.init(this);


        /*Create data map to be use for shark sponsors*/
        createSharkSponsorMap();
    }

    //TODO : Replace with something else, this can not be necessary
    private void createSharkSponsorMap()
    {
        for(String x : getResources().getStringArray(R.array.sponsor_sharks))
        {
            switch (x)
            {
                case "ADVANCED ROOFING":
                    sharkSponsors.put(x, "Advanced Roofing Inc.");
                    break;
                case "AFTCO":
                    sharkSponsors.put(x, "American Fishing Tackle Company");
                    break;

                case "BREGARDO PRIMARY":
                    sharkSponsors.put(x, "Virgin Unite");
                    break;

                case "BRUCE":
                    sharkSponsors.put(x, "Hidden Oaks Middle School");
                    break;

                case "CHARLOTTE":
                    sharkSponsors.put(x, "Charlotte Latin / Finke Family");

                case "DIVERS DIRECT":
                    sharkSponsors.put(x, "Divers Direct Inc.");

                case "EBENEZER PRIMARY":
                    sharkSponsors.put(x, "Virgin Unite");
                    break;

                case "ESELYN CENTER":
                    sharkSponsors.put(x, "Virgin Unite");
                    break;

                case "GRYCON":
                    sharkSponsors.put(x, "Grycon, LLC");
                    break;

                case "HELLS BAY":
                    sharkSponsors.put(x, "Hells Bay Boatworks");
                    break;

                case "PALMETTO MOON":
                    sharkSponsors.put(x, "Palmetto Moon Inc");
                    break;

            }



        }

    }

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }
}
