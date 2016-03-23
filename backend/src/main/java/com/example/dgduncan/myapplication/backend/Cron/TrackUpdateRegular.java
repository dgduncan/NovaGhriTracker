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

/***
 * Written by: Derek Duncan
 * Date: 3/23/2016
 *
 * This file contains the HttpServlet that updates the Google Cloud Datastore. It was written in
 * such a way as to only update from latest to earliest. If there is an error and a row was missed
 * such that if there is an error uploading in the middle, say in the datastore we see 5->6->8->9,
 * 7 will not be uploaded even if it eventually exists in the CSV. Go through and check EVERY single
 * row, use TrackUpdateFull
 *
 */

public class TrackUpdateRegular extends HttpServlet
{
    /***
     * ArrayList containing the branches for all of the CSVs
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
            "whitemarlin.csv"
    ));

    /***
     * The ArrayLists that contain an array for each distinct shark
     */
    private ArrayList<ArrayList<String[]>> sharkLists = new ArrayList<>();

    /***
     * The CSVreader object used to parse through the CSVs
     */
    private CSVReader reader;

    /***
     * Reference to the datastore used for saves
     */
    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    /***
     * The indexs in the CSV that contain the specified information
     */
    private int latitudeIndex;
    private int longitudeIndex;
    private int nameIndex;
    private int sequenceIndex;

    /***
     * The number of times that the row has been retried. This is used to help get over errors in
     * the CSVs
     */
    private int tryAgain = 0;

    /***
     * The number of entities that have been uploaded
     */
    private int updateCount = 0;

    /***
     * Logger used to log to GAE
     */
    private static final Logger logger = Logger.getLogger(TrackEndpoint.class.getName());

    /***
     * The responder to the http get request
     * @param req the servlet request
     * @param resp the response servlet
     * @throws IOException if anything foes wrong
     */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException
    {
        for(String csvBranch : branchList)
        {

            buildCSVReader(csvBranch);

            ArrayList<String[]> csvData = new ArrayList<>(reader.readAll());

            getCsvIndexes(csvData.get(0));

            csvData.remove(0);

            splitIntoSharkArrays(csvData);

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


    /***
     * Method used to create objects and references required to read a CSV stream
     * @param csvBranch the branch string that signifies what CSV we are looking through
     */
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

        BufferedReader in = null;

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

    /***
     * Method used to build the entity we want to upload and put into the datastore
     * @param x the string[] that contains the information we want to upload
     */
    private void buildEntity(String[] x)
    {
        Entity entity = new Entity("Track");

        entity.setProperty("name", x[nameIndex]);
        entity.setProperty("latitude", x[latitudeIndex]);
        entity.setProperty("longitude", x[longitudeIndex]);
        entity.setProperty("sequence", Integer.parseInt(x[sequenceIndex]));

        datastore.put(entity);

    }

    /***
     * Method used to check whether an entity exists in the datastore
     * @param currentRow the string[] that contains the data we want to check if it exists in the
     *                   datastore
     * @return boolean value if the query returns something
     */
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

    /***
     * Get the column numbers that contain the information we need
     * @param metaData the first row of the CSV
     */
    private void getCsvIndexes(String[] metaData)
    {
        List<String> metaDataArray = Arrays.asList(metaData);

        latitudeIndex = metaDataArray.indexOf("latitude");

        longitudeIndex = metaDataArray.indexOf("longitude");

        nameIndex = metaDataArray.indexOf("shark");

        sequenceIndex = metaDataArray.indexOf("sequence");


    }

    /***
     * Method to split the array received from the CSV into an array for each distinct animal
     * @param x the array containing all rows from the CSV
     */
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

    /***
     * CustomComparator class written to order by descending sequence
     */
    public class CustomComparator implements Comparator<String[]>
    {
        @Override
        public int compare(String[] o1, String[] o2)
        {
            return Integer.parseInt(o2[sequenceIndex]) - Integer.parseInt(o1[sequenceIndex]);
        }
    }


}
