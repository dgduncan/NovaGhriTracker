package com.upperz.sharktracker;

import android.app.Application;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.maps.model.Marker;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import net.danlew.android.joda.JodaTimeAndroid;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MyApplication extends Application
{
    public final static HashMap<String, Object> params = new HashMap<>();
    public final static HashMap<String, Object> sharkTrackParams = new HashMap<>();
    public static List<ParseObject> sharks = new ArrayList<>();
    public final static Map<String, String> dimensions = new HashMap<>();
    public final static Map<String, String> sharkSponsors = new HashMap<>();

    public static Boolean mLoadSharkTrackFlag = false;
    public static Marker mCurrentMarkerSelected;
    public static ParseObject mCurrentObjectParseObject;
    public static String mCurrentSharkSelected;

    private String TAG = "MyApplication";


    public void onCreate()
    {
        super.onCreate();

        JodaTimeAndroid.init(this);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "IwtHq5dLlt0Wasp3tQ78g9sSeBFItdOQomd5BGb5",
                "cdX1CgLdRYRRPBBUX6jYMFscjaq6Myxc1fn47Z0l");

        parseSignUp();
        createSharkSponsorMap();
    }

    private void parseSignUp()
    {
        String deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        ParseUser user = new ParseUser();
        user.setUsername(deviceId);
        user.setPassword("password");
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null)
                    Log.i(TAG, "User created");
                else
                    Log.d(TAG, e.toString());
            }
        });

    }

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

    /*private void parseLogIn() {

        deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);


        ParseUser.logInInBackground(deviceId, "password", new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (e == null) {
                    Log.i(TAG, "User Logged In Correctly");
                } else {
                    Log.i(TAG, "Log In Failed");
                    if(e.getCode() == 101)
                        parseSignUp();
                }
            }
        });




    }*/
}
