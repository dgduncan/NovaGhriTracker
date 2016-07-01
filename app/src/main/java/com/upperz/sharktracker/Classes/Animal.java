package com.upperz.sharktracker.Classes;

import org.json.JSONException;
import org.json.JSONObject;

public class Animal
{
    public boolean recent;

    public double latitude_current;
    public double latitude_initial;
    public double longitude_current;
    public double longitude_initial;

    public int days_tracked;
    public int sequence;

    public String common_name;
    public String date;
    public String name;
    public String sex;
    public String size;
    public String species;
    public String tagging_video;




    public Animal(JSONObject jsonObject) throws JSONException {


        this.recent = jsonObject.getBoolean("recent");

        this.latitude_current = jsonObject.getDouble("latitude_current");
        this.latitude_initial = jsonObject.getDouble("latitude_initial");
        this.longitude_current = jsonObject.getDouble("longitude_current");
        this.longitude_initial = jsonObject.getDouble("longitude_initial");

        this.days_tracked = jsonObject.getInt("days_tracked");
        this.sequence = jsonObject.getInt("sequence");

        this.common_name = jsonObject.getString("common_name");
        this.date = jsonObject.getString("date");
        this.name = jsonObject.getString("name");
        this.sex = jsonObject.getString("sex");
        this.size = jsonObject.getString("size");
        this.species = jsonObject.getString("species");
        this.tagging_video = jsonObject.getString("tagging_video");
    }


}
