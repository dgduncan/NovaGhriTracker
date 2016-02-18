package com.upperz.sharktracker.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.upperz.sharktracker.Classes.Animal;
import com.upperz.sharktracker.MyApplication;
import com.upperz.sharktracker.R;

import java.util.List;

public class SharkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shark);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra("name"));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("trackName", getIntent().getStringExtra("name"));
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });


        updateViews();
        getDistanceTraveled();


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateViews()
    {
        Animal animal = MyApplication.animals.get(getIntent().getStringExtra("name"));

        TextView sex = (TextView)findViewById(R.id.sex);
        TextView latitude = (TextView)findViewById(R.id.latitude);
        TextView longitude = (TextView)findViewById(R.id.longitude);
        TextView date = (TextView)findViewById(R.id.date);
        TextView days = (TextView)findViewById(R.id.days);
        TextView name = (TextView)findViewById(R.id.name);


        name.setText(animal.name);
        date.setText(animal.date);
        days.setText(animal.days);
        sex.setText(animal.sex);
        latitude.setText(String.valueOf(animal.latestLocation.getPosition().latitude));
        longitude.setText(String.valueOf(animal.latestLocation.getPosition().longitude));

    }

    public static float distFrom(double lat1, double lng1, double lat2, double lng2)
    {
        double earthRadius = 6371000;
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return (float) (earthRadius * c);
    }

    public void getDistanceTraveled()
    {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Animals");
        query.whereEqualTo("shark", getIntent().getStringExtra("name"));
        query.setLimit(1000);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if(e == null)
                {
                    float distance = 0;

                    for(int i = 0; i < list.size()-1; i++)
                    {
                        distance = distance + distFrom(Double.valueOf(list.get(i).getString("latitude")),
                                Double.valueOf(list.get(i).getString("longitude")),
                                Double.valueOf(list.get(i+1).getString("latitude")),
                                Double.valueOf(list.get(i+1).getString("longitude")));
                    }

                    TextView daysTracked = (TextView)findViewById(R.id.daysTracked);
                    daysTracked.setText(String.valueOf((int) (distance * 0.000621371)));
                }
            }
        });
    }
}
