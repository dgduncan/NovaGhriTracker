package com.upperz.sharktracker.Classes;


import org.json.JSONException;
import org.json.JSONObject;

public class Track
{
    public double latitude;
    public double longitude;

    public int sequence;

    public String name;

    public Track(JSONObject jsonObject) throws JSONException
    {

        this.latitude = jsonObject.getDouble("latitude");
        this.longitude = jsonObject.getDouble("longitude");
        this.sequence = jsonObject.getInt("sequence");
        this.name = jsonObject.getString("name");

    }
}
