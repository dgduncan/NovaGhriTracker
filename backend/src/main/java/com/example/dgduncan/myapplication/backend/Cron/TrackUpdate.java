package com.example.dgduncan.myapplication.backend.Cron;

import com.example.dgduncan.myapplication.backend.Endpoints.TrackEndpoint;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
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
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TrackUpdate extends HttpServlet
{

    private ArrayList<String[]> x;

    //private ArrayList<ArrayList<String[]>> sharkLists = new ArrayList<>();

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

    private HashSet<String> animalNames = new HashSet<>();


    /***
     * The indexs in the CSV that contain the specified information
     */
    private int latitudeIndex;
    private int longitudeIndex;
    private int nameIndex;
    private int sequenceIndex;

    private BufferedReader in;

    private CSVReader reader;

    private int updateCount = 0;

    private static final Logger logger = Logger.getLogger(TrackEndpoint.class.getName());

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException
    {

        for(String csvBranch : branchList)
        {

            buildCSVReader(csvBranch);

            x = new ArrayList<>(reader.readAll());

            getCsvIndexes(x.get(0));

            getAnimalNames();

            x.remove(0);

            //splitIntoSharkArrays(x);

            /*
            for(ArrayList<String[]> currentShark : sharkLists)
            {
                //Collections.sort(currentShark, new CustomComparator());


                for(int i = currentShark.size()-1; i >= 0; i--)
                {


                    if(checkIfTrackExists(currentShark.get(i)))
                    {
                        logger.info("Duplicate found");
                        break;

                    }

                    else {
                        updateCount++;
                        buildEntity(currentShark.get(i));
                    }

                    updateCount++;
                    buildEntity(currentShark.get(i));
                }

            }
            */

            queryTrackByName(animalNames);


            reader.close();
            in.close();

            animalNames.clear();

        }

        logger.info(String.valueOf(updateCount ) + " Entities Added");

        updateCount = 0;

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

    private void queryTrackByName(HashSet<String> name)
    {

        Query.Filter queryFilter = new Query.FilterPredicate("name", Query.FilterOperator.IN, name);

        Query q = new Query("Track").setFilter(queryFilter);

        PreparedQuery preparedQuery = datastore.prepare(q);

        List<Entity> tracks = preparedQuery.asList(FetchOptions.Builder.withLimit(10000));







        for(String[] currentRow : x)
        {
            boolean updateFlag = true;


            for(Entity currentTrack : tracks)
            {
                if((currentRow[nameIndex].equals(currentTrack.getProperty("name"))) && (currentRow[sequenceIndex]).equals(String.valueOf(currentTrack.getProperty("sequence"))))
                {
                    updateFlag = false;
                    break;
                }

            }

            if(updateFlag)
            {
                updateCount++;
                buildEntity(currentRow);
            }


        }



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

    /*
    public class CustomComparator implements Comparator<String[]>
    {
        @Override
        public int compare(String[] o1, String[] o2)
        {
            return Integer.parseInt(o2[sequenceIndex]) - Integer.parseInt(o1[sequenceIndex]);
        }
    }*/

    /*
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
            }
        }

        sharkLists.add(currentSharkArray);



        //currentSharkArray.clear();

    }
    */

    /*
    private boolean checkIfTrackExists(String [] currentRow)
    {

        Query.Filter queryFilter = new Query.FilterPredicate("name", Query.FilterOperator.EQUAL, currentRow[nameIndex]);
        Query.Filter queryFilter2 = new Query.FilterPredicate("sequence", Query.FilterOperator.EQUAL, Integer.parseInt(currentRow[sequenceIndex]));

        Query.Filter finalFilter = Query.CompositeFilterOperator.and(queryFilter, queryFilter2);

        Query q = new Query("Track").setFilter(queryFilter).setFilter(finalFilter);

        PreparedQuery preparedQuery = datastore.prepare(q);

        Entity entity = preparedQuery.asSingleEntity();

        return entity != null;


    }
    */

    private void getAnimalNames()
    {
        for(String[] y : x)
        {
            animalNames.add(y[nameIndex]);
        }

    }


}
