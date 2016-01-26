package com.upperz.sharktracker;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Random;

public class SponsorFragment extends Fragment
{

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v =inflater.inflate(R.layout.fragment_sponsor,container,false);


        WebView myWebView = (WebView) v.findViewById(R.id.webview);

        Random rand = new Random();

        final Activity activity = getActivity();

        myWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                activity.setTitle("Loading...");
                activity.setProgress(progress * 100);
                if (progress == 100)
                    activity.setTitle("Sponsors");
            }
        });

        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }
        });

        myWebView.loadUrl("http://www.nova.edu/ocean/ghri/tracking/makomexico_sponsors.htm");

        switch (rand.nextInt((13) + 1))
        {
            case 0:
                myWebView.loadUrl("http://www.nova.edu/ocean/ghri/tracking/nepmakosharks_sponsors.htm");
                break;
            case 1:
                myWebView.loadUrl("http://www.nova.edu/ocean/ghri/tracking/makomexico_sponsors.htm");
                break;
            case 2:
                myWebView.loadUrl("http://www.nova.edu/ocean/ghri/tracking/mako_sponsors.htm");
                break;
            case 3:
                myWebView.loadUrl("http://www.nova.edu/ocean/ghri/tracking/makomexico_sponsors.htm");
                break;
            case 4:
                myWebView.loadUrl("http://www.nova.edu/ocean/ghri/tracking/australia_sponsors.htm");
                break;
            case 5:
                myWebView.loadUrl("http://www.nova.edu/ocean/ghri/tracking/cayman_sponsors_tiger.htm");
                break;
            case 6:
                myWebView.loadUrl("http://www.nova.edu/ocean/ghri/tracking/grandbahama_sponsors.htm");
                break;
            case 7:
                myWebView.loadUrl("http://www.nova.edu/ocean/ghri/tracking/bermuda_sponsors_2009.htm");
                break;
            case 8:
                myWebView.loadUrl("http://www.nova.edu/ocean/ghri/tracking/cayman_sponsors_owt.htm");
                break;
            case 9:
                myWebView.loadUrl("http://www.nova.edu/ocean/ghri/tracking/nepmakosharks_sponsors.htm");
                break;
            case 10:
                myWebView.loadUrl("http://www.nova.edu/ocean/ghri/tracking/bluemarlin_sponsors.htm");
                break;
            case 11:
                myWebView.loadUrl("http://www.nova.edu/ocean/ghri/tracking/sailfish_sponsors.htm");
                break;
            case 12:
                myWebView.loadUrl("http://www.nova.edu/ocean/ghri/tracking/whitemarlin_sponsors.htm");
                break;
            case 13:
                myWebView.loadUrl("http://www.nova.edu/ocean/ghri/tracking/makomexico_sponsors.htm");
                break;
            default:
                myWebView.loadUrl("http://www.nova.edu/ocean/ghri/tracking/makomexico_sponsors.htm");

        }







        return v;
    }

}
