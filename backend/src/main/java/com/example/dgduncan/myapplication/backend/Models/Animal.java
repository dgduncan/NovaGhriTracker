package com.example.dgduncan.myapplication.backend.Models;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class Animal {

    @Id
    public Long id;

    private String common_name;

    private String latitude;

    private String longitude;

    private String name;

    private String sex;

    private String size;

    private String species;

    private String tagging_video;



    public String getName()
    {
        return this.name;
    }

    public Animal(){}

    public Animal(String name)
    {
        this.name = name;
    }

}