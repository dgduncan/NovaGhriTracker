package com.upperz.sharktracker.Classes;


import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.upperz.sharktracker.R;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

public class Animal
{
    public ArrayList<ParseGeoPoint> allLocations = new ArrayList<>();
    public MarkerOptions firstLocation;
    public MarkerOptions latestLocation;

    public boolean activeFlag;
    public String name;
    public String date;
    public String sex;
    public String days;

    public Animal(ParseObject animal)
    {

        this.activeFlag = checkIfRecent(animal);
        this.name = animal.getString("shark");
        this.date = animal.getString("date");
        this.days = animal.getString("sequence");
        this.sex = animal.getString("sex");
        this.latestLocation = createMarkerOptions(animal);

    }

    private boolean checkIfRecent(ParseObject objectToCheckIfRecent) {

        DateTimeFormatter dateTimeFormat = DateTimeFormat.forPattern("MM/dd/yyyy");
        DateTime dateTime = dateTimeFormat.parseDateTime(objectToCheckIfRecent.getString("date"));

        return Days.daysBetween(dateTime, new LocalDateTime().toDateTime()).getDays() <= 14;

    }

    private MarkerOptions createMarkerOptions(@NonNull ParseObject animal)
    {
        if(this.activeFlag)
        {
            return new MarkerOptions()
                    .position(new LatLng(Double.valueOf(animal.getString("latitude")), Double.valueOf(animal.getString("longitude"))))
                    .title(animal.getString("shark"))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.green_measle));
        }
        else
        {
            return new MarkerOptions()
                    .position(new LatLng(Double.valueOf(animal.getString("latitude")), Double.valueOf(animal.getString("longitude"))))
                    .title(animal.getString("shark"))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_measle));
        }
    }



}
