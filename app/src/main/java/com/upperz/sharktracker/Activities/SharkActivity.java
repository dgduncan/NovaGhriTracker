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

import com.upperz.sharktracker.Classes.Animal;
import com.upperz.sharktracker.MyApplication;
import com.upperz.sharktracker.R;

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
}
