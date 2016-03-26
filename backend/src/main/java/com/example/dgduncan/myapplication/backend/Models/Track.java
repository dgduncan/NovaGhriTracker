package com.example.dgduncan.myapplication.backend.Models;

import com.google.appengine.api.datastore.GeoPt;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class Track {

    @Id
    public Long id;

    public GeoPt location;

    @Index
    public String name;

    @Index
    public int sequence;

    public Track()
    {}

    public Track(String name, GeoPt location, int sequence)
    {
        this.name = name;
        this.location = location;
        this.sequence = sequence;

    }


}
