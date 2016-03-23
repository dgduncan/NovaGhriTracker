package com.example.dgduncan.myapplication.backend.Models;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class Track {

    @Id
    public long id;

    public String date;

    public String latitude;

    public String longitude;

    @Index
    public String name;

    @Index
    public String sequence;

    public Track()
    {}


}
