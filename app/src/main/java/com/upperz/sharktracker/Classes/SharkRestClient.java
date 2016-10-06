package com.upperz.sharktracker.Classes;

import com.loopj.android.http.AsyncHttpClient;

public class SharkRestClient {

    private static final String BASE_URL = "http://104.197.207.240:8080/api/";

    private static AsyncHttpClient client = new AsyncHttpClient();



    private static String getAbsoluteURL(String relativeURL)
    {
        return BASE_URL + relativeURL;
    }
}
