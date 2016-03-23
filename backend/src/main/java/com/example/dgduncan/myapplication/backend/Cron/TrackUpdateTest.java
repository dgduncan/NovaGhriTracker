package com.example.dgduncan.myapplication.backend.Cron;

import com.example.dgduncan.myapplication.backend.Endpoints.TrackEndpoint;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
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

public class TrackUpdateTest extends HttpServlet
{

    private int updateCount = 0;

    private int tryAgain = 0;

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();


    /***
     * The indexs in the CSV that contain the specified information
     */
    private int latitudeIndex;
    private int longitudeIndex;
    private int nameIndex;
    private int sequenceIndex;

    private ArrayList<String[]> x;

    private BufferedReader in;

    private CSVReader reader;



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
            "whitemarlin.csv"
    ));

    private ArrayList<ArrayList<String[]>> sharkLists = new ArrayList<>();

    private static final Logger logger = Logger.getLogger(TrackEndpoint.class.getName());

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException
    {
        for(String csvBranch : branchList)
        {

            buildCSVReader(csvBranch);

            x = new ArrayList<>(reader.readAll());

            getCsvIndexes(x.get(0));

            x.remove(0);

            splitIntoSharkArrays(x);

            for(ArrayList<String[]> currentShark : sharkLists)
            {
                Collections.sort(currentShark, new CustomComparator());

                for(String[] currentRow : currentShark)
                {
                    if(tryAgain == 1)
                    {
                        tryAgain = 0;

                        if(checkIfTrackExists(currentRow))
                        {
                            break;
                        }
                    }

                    if((checkIfTrackExists(currentRow)))
                    {
                        tryAgain++;
                    }

                    else {
                        updateCount++;
                        buildEntity(currentRow);
                    }


                }
            }

            sharkLists = new ArrayList<>();


        }

        logger.info(String.valueOf(updateCount ) + " Entities Added");

        updateCount = 0;


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

    private void buildCSVReader(String csvBranch)
    {
        URL stockURL = null;

        try
        {
            String root = "http://cnso.nova.edu/sharktracking/sharkmap/controlfiles/";
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

    private void getCsvIndexes(String[] metaData)
    {
        List<String> metaDataArray = Arrays.asList(metaData);

        latitudeIndex = metaDataArray.indexOf("latitude");

        longitudeIndex = metaDataArray.indexOf("longitude");

        nameIndex = metaDataArray.indexOf("shark");

        sequenceIndex = metaDataArray.indexOf("sequence");


    }


    public class CustomComparator implements Comparator<String[]>
    {
        @Override
        public int compare(String[] o1, String[] o2)
        {
            return Integer.parseInt(o2[sequenceIndex]) - Integer.parseInt(o1[sequenceIndex]);
        }
    }

    private boolean checkIfTrackExists(String [] currentRow)
    {

        //logger.info("Query made");

        Query.Filter queryFilter = new Query.FilterPredicate("name", Query.FilterOperator.EQUAL, currentRow[nameIndex]);
        Query.Filter queryFilter2 = new Query.FilterPredicate("sequence", Query.FilterOperator.EQUAL, Integer.parseInt(currentRow[sequenceIndex]));

        Query.Filter finalFilter = Query.CompositeFilterOperator.and(queryFilter, queryFilter2);

        Query q = new Query("Track").setFilter(queryFilter).setFilter(finalFilter);

        PreparedQuery preparedQuery = datastore.prepare(q);

        Entity entity = preparedQuery.asSingleEntity();

        return entity != null;


    }

    private void buildEntity(String[] x)
    {
        Entity entity = new Entity("Track");

        entity.setProperty("name", x[nameIndex]);
        entity.setProperty("latitude", x[latitudeIndex]);
        entity.setProperty("longitude", x[longitudeIndex]);
        entity.setProperty("sequence", Integer.parseInt(x[sequenceIndex]));

        datastore.put(entity);

    }
}
