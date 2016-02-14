package com.upperz.sharktracker.Fragments;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.upperz.sharktracker.MyApplication;
import com.upperz.sharktracker.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import info.hoang8f.widget.FButton;


public class SharkFragment extends Fragment {

    public FButton fButton;
    public ImageView sharkImage;
    public TextView nameText;
    public TextView sexText;
    public TextView dateText;
    public TextView timeText;
    public TextView distanceText;

    public String TAG = "SharkFragment";


    @Override
    public void onResume()
    {
        super.onResume();

        //update();

        Log.d(TAG, "onResume Called");
    }
    @Override
    public void onPause()
    {
        super.onPause();

        Log.d(TAG, "onPause Called");
    }

    @Override
     public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView Called");




        View v =inflater.inflate(R.layout.fragment_shark,container,false);

        fButton = (FButton)v.findViewById(R.id.flatButton);
        fButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadSharkTrack();
            }
        });


        if (MyApplication.mCurrentSharkSelected == null) {
            v.findViewById(R.id.textView3).setVisibility(View.VISIBLE);
            v.findViewById(R.id.textView4).setVisibility(View.VISIBLE);
            v.findViewById(R.id.profile_image).setVisibility(View.GONE);
            v.findViewById(R.id.shark_info).setVisibility(View.GONE);

            return v;
        }
        else
        {
            v.findViewById(R.id.textView3).setVisibility(View.GONE);
            v.findViewById(R.id.textView4).setVisibility(View.GONE);
            v.findViewById(R.id.profile_image).setVisibility(View.VISIBLE);
            v.findViewById(R.id.shark_info).setVisibility(View.VISIBLE);

            distanceText = (TextView)v.findViewById(R.id.sharkDistanceTraveled);
            nameText = (TextView)v.findViewById(R.id.sharkActivityName);
            sharkImage = (ImageView)v.findViewById(R.id.profile_image);

            //sharkImage.setImageResource(R.drawable.whitemarlin_1);

            loadApproptiateImage(v, MyApplication.mCurrentObjectParseObject);
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Animals");
            query.whereEqualTo("shark", "ADVANCED ROOFING");
            query.setLimit(1000);
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> scoreList, ParseException e) {
                    if (e == null) {

                        float distance = 0;

                        for(int i = 0; i < scoreList.size()-1; i++) {
                            distance = distance + distFrom( Double.valueOf(scoreList.get(i).getString("latitude")),
                                    Double.valueOf(scoreList.get(i).getString("longitude")),
                                    Double.valueOf(scoreList.get(i + 1).getString("latitude")),
                                    Double.valueOf(scoreList.get(i + 1).getString("longitude")));
                        }

                        distanceText.setText(String.valueOf((int) (distance * 0.000621371)));

                    } else {
                        Log.d(TAG, e.getMessage());
                    }
                }
            });


            return v;

        }


    }

    public static float distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return (float) (earthRadius * c);
    }

    public void loadApproptiateImage(View view, ParseObject x)
    {

        sharkImage = (ImageView)view.findViewById(R.id.profile_image);
        switch(x.getString("project"))
        {
            case "bluemarlin":
                sharkImage.setImageResource(R.drawable.whitemarlin_1);
                break;
            case "caribbeanmakosharks":
                sharkImage.setImageResource(R.drawable.mako_1);
                break;
            case "enpmakosharks":
                sharkImage.setImageResource(R.drawable.mako_1);
                break;
            case "makosharks":
                sharkImage.setImageResource(R.drawable.mako_1);
                break;
            case "makosharksmexico":
                sharkImage.setImageResource(R.drawable.mako_1);
                break;
            case "owtsharks":
                sharkImage.setImageResource(R.drawable.owt_orig);
                break;
            case "sailfish":
                sharkImage.setImageResource(R.drawable.sailfish3);
                break;
            case "sandtiger":
                sharkImage.setImageResource(R.drawable.tiger_orig);
                break;
            case "tigerbermuda2009":
                sharkImage.setImageResource(R.drawable.tiger_orig);
                break;
            case "tigerbermuda2010":
                sharkImage.setImageResource(R.drawable.tiger_orig);
                break;
            case "tigerbermuda2011_14":
                sharkImage.setImageResource(R.drawable.tiger_orig);
                break;
            case "tigergrandbahama":
                sharkImage.setImageResource(R.drawable.tiger_orig);
                break;
            case "tigergrandcayman":
                sharkImage.setImageResource(R.drawable.tiger_orig);
                break;
            case "tigerwesternaustralia":
                sharkImage.setImageResource(R.drawable.tiger_orig);
                break;
            case "whitemarlin":
                sharkImage.setImageResource(R.drawable.whitemarlin_1);
                break;
            case "islamakorace":
                sharkImage.setImageResource(R.drawable.mako_1);
                break;
            default:
                Log.d(TAG, "Default Chosen");
                sharkImage.setImageResource(R.drawable.whitemarlin_1);

        }
    }

    public void update() {

        Log.d(TAG, "update called");


        if(MyApplication.mCurrentSharkSelected == null)
        {
            getActivity().findViewById(R.id.textView3).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.textView4).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.profile_image).setVisibility(View.GONE);
            getActivity().findViewById(R.id.shark_info).setVisibility(View.GONE);
        }

        else
        {
            getActivity().findViewById(R.id.textView3).setVisibility(View.GONE);
            getActivity().findViewById(R.id.textView4).setVisibility(View.GONE);
            getActivity().findViewById(R.id.profile_image).setVisibility(View.VISIBLE);
            getActivity().findViewById(R.id.shark_info).setVisibility(View.VISIBLE);

            distanceText = (TextView)getActivity().findViewById(R.id.sharkDistanceTraveled);
            nameText = (TextView)getActivity().findViewById(R.id.sharkActivityName);
            sexText = (TextView)getActivity().findViewById(R.id.sharkActivitySex);
            dateText = (TextView)getActivity().findViewById(R.id.sharkActivityDate);
            timeText = (TextView)getActivity().findViewById(R.id.sharkActivityTime);

            sharkImage = (ImageView)getActivity().findViewById(R.id.profile_image);

            loadApproptiateImage(getView(), MyApplication.mCurrentObjectParseObject);

            ParseQuery<ParseObject> mDistanceQuery = ParseQuery.getQuery("Animals");
            mDistanceQuery.whereEqualTo("shark", MyApplication.mCurrentSharkSelected);
            mDistanceQuery.setLimit(1000);
            mDistanceQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> scoreList, ParseException e) {
                    if (e == null)
                    {
                        float distance = 0;

                        for(int i = 0; i < scoreList.size()-1; i++) {
                            distance = distance + distFrom( Double.valueOf(scoreList.get(i).getString("latitude")),
                                    Double.valueOf(scoreList.get(i).getString("longitude")),
                                    Double.valueOf(scoreList.get(i + 1).getString("latitude")),
                                    Double.valueOf(scoreList.get(i + 1).getString("longitude")));
                        }
                        distanceText.setText(String.valueOf((int) (distance * 0.000621371)));


                    } else {
                        Log.d("SharkFragment", e.toString());
                    }

                }
            });

            for(ParseObject x :  MyApplication.sharks)
            {
                if(x.getString("shark").equals(MyApplication.mCurrentSharkSelected))
                {
                    nameText.setText(x.getString("shark"));
                    sexText.setText(x.getString("sex"));
                    dateText.setText(x.getString("date"));
                    timeText.setText(x.getString("time"));
                }
            }
        }

    }

    public void loadSharkTrack()
    {
        MyApplication.mLoadSharkTrackFlag = true;
        ViewPager vp = (ViewPager) getActivity().findViewById(R.id.container);
        TabLayout tl = (TabLayout) getActivity().findViewById(R.id.tabs);
        vp.setCurrentItem(0);
        tl.setScrollPosition(0, 0, true);
    }

}
