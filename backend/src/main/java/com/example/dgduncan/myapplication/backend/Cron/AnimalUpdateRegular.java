package com.example.dgduncan.myapplication.backend.Cron;

import com.example.dgduncan.myapplication.backend.Endpoints.TrackEndpoint;
import com.example.dgduncan.myapplication.backend.Models.Animal;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.repackaged.org.joda.time.DateTime;
import com.google.appengine.repackaged.org.joda.time.Days;
import com.google.appengine.repackaged.org.joda.time.LocalDateTime;
import com.google.appengine.repackaged.org.joda.time.format.DateTimeFormat;
import com.google.appengine.repackaged.org.joda.time.format.DateTimeFormatter;
import com.googlecode.objectify.ObjectifyService;
import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AnimalUpdateRegular extends HttpServlet
{

    /***
     * ArrayList containing the "branches" contianing the CSV files
     */
    private ArrayList<String> branchList = new ArrayList<>(Arrays.asList(
            "bluemarlin.csv",
            "caribbeanmakosharks.csv",
            "enpmakosharks.csv",
            "makosharks.csv",
            "makosharksmexico.csv",
            "owtsharks.csv",
            "sailfish.csv",
            "sandtiger.csv",
            "tigerbermuda2009.csv",
            "tigerbermuda2010.csv",
            "tigerbermuda2011-14.csv",
            "tigergrandbahama.csv",
            "tigergrandcayman.csv",
            "tigerwesternaustralia.csv",
            "whitemarlin.csv"));

    private ArrayList<ArrayList<String[]>> sharkLists = new ArrayList<>();

    /***
     * The indexes in the CSV that contain the information
     */
    private int commonNameIndex;
    private int dateIndex;
    private int latitudeIndex;
    private int longitudeIndex;
    private int nameIndex;
    private int sexIndex;
    private int sizeIndex;
    private int speciesIndex;
    private int taggingVideoIndex;


    private String root = "http://cnso.nova.edu/sharktracking/sharkmap/controlfiles/";



    private BufferedReader in;

    private CSVReader reader;

    /***
     * The DataStore reference used to to query, put, and get
     */
    private DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();

    private static final Logger logger = Logger.getLogger(TrackEndpoint.class.getName());

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(Animal.class);
    }




    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException
    {

        deleteAllAnimals();

        for(String csvBranch : branchList)
        {

            buildCSVReader(csvBranch);

            ArrayList<String[]> x = new ArrayList<>(reader.readAll());

            getCsvIndexes(x.get(0));


            x.remove(0);

            splitIntoSharkArrays(x);

            for(ArrayList<String[]> currentShark : sharkLists)
            {
                Collections.sort(currentShark, new CustomComparator());

                buildEntity(x.get(0), currentShark.get(0), currentShark.get(currentShark.size()-1));
            }


            reader.close();
            in.close();

            sharkLists.clear();

        }

    }

    private void getCsvIndexes(String[] metaData)
    {
        List<String> metaDataArray = Arrays.asList(metaData);

        commonNameIndex = metaDataArray.indexOf("commonname");

        dateIndex = metaDataArray.indexOf("date");

        latitudeIndex = metaDataArray.indexOf("latitude");

        longitudeIndex = metaDataArray.indexOf("longitude");

        nameIndex = metaDataArray.indexOf("shark");

        sexIndex = metaDataArray.indexOf("sex");

        sizeIndex = metaDataArray.indexOf("size");

        speciesIndex = metaDataArray.indexOf("species");

        taggingVideoIndex = metaDataArray.indexOf("taggingvideo");

    }

    private void buildCSVReader(String csvBranch)
    {
        URL stockURL = null;

        try
        {
            stockURL = new URL(root + csvBranch);
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }

        in = null;

        try
        {
            if (stockURL != null)
            {
                in = new BufferedReader(new InputStreamReader(stockURL.openStream()));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        if (in != null)
        {
            reader = new CSVReader(in);
        }


    }

    private void splitIntoSharkArrays(ArrayList<String[]> x)
    {
        String currentShark = x.get(0)[nameIndex];

        ArrayList<String[]> currentSharkArray = new ArrayList<>();

        for(String[] currentRow : x)
        {
            if(currentRow[nameIndex].equals(currentShark))
            {
                currentSharkArray.add(currentRow);

            }

            else
            {
                sharkLists.add(currentSharkArray);

                currentSharkArray = new ArrayList<>();

                currentShark = currentRow[nameIndex];

                currentSharkArray.add(currentRow);
            }
        }

        sharkLists.add(currentSharkArray);



        //currentSharkArray.clear();

    }

    public class CustomComparator implements Comparator<String[]>
    {
        @Override
        public int compare(String[] o1, String[] o2)
        {
            return Integer.parseInt(o1[0]) - Integer.parseInt(o2[0]);
        }
    }


    private void buildEntity(String[] beginning, String[] first, String[] x)
    {

        Entity entity = new Entity("Animal");

        entity.setProperty("name", x[nameIndex]);
        entity.setProperty("species", x[speciesIndex]);

        if(x[sexIndex].equals(""))
        {
            entity.setProperty("sex", "unknown");
        }

        else
        {
            entity.setProperty("sex", x[sexIndex]);
        }

        entity.setProperty("common_name", beginning[commonNameIndex]);

        if((first[sizeIndex].equals("")))
        {
            entity.setProperty("size", "unknown");
        }
        else
        {
            entity.setProperty("size", first[sizeIndex]);
        }

        if(beginning[taggingVideoIndex].equals(""))
        {
            entity.setProperty("tagging_video", "unknown");
        }
        else
        {
            entity.setProperty("tagging_video", beginning[taggingVideoIndex]);
        }

        entity.setProperty("latitude", x[latitudeIndex]);
        entity.setProperty("longitude", x[longitudeIndex]);

        String date = x[dateIndex];

        if((date.indexOf(' ')) != -1)
        {
            date = date.substring(0, date.indexOf(' '));
        }


        entity.setProperty("date", date);

        entity.setProperty("sequence", Integer.parseInt(x[0]));

        entity.setProperty("recent", checkIfRecent(date));

        datastoreService.put(entity);

    }

    private void deleteAllAnimals()
    {

        Query q = new Query("Animal").setKeysOnly();

        PreparedQuery preparedQuery = datastoreService.prepare(q);


        for (Entity entity : preparedQuery.asIterable()) datastoreService.delete(entity.getKey());

    }

    private boolean checkIfRecent(String date) {

        DateTimeFormatter dateTimeFormat = DateTimeFormat.forPattern("MM/dd/yyyy");
        DateTime dateTime = dateTimeFormat.parseDateTime(date);

        return Days.daysBetween(dateTime, new LocalDateTime().toDateTime()).getDays() <= 14;

    }
}






