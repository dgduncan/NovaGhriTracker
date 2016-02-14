package com.upperz.sharktracker.Activities;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.parse.ParseAnalytics;
import com.upperz.sharktracker.Fragments.ListFragment;
import com.upperz.sharktracker.Fragments.MapFragment;
import com.upperz.sharktracker.Fragments.SharkFragment;
import com.upperz.sharktracker.Fragments.SponsorFragment;
import com.upperz.sharktracker.MyApplication;
import com.upperz.sharktracker.R;

import me.drakeet.materialdialog.MaterialDialog;

public class MainTabbedActivity extends AppCompatActivity {


    public SharkFragment sf;
    public MapFragment mf;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_tabbed);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ParseAnalytics.trackAppOpenedInBackground(getIntent());



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("GHRI Tracking Projects");
        toolbar.setNavigationIcon(R.drawable.rsz_11rsz_1logo);
        //toolbar.setLogo(R.drawable.ghri_logo);
        setSupportActionBar(toolbar);

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


        final ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        //mViewPager.setOffscreenPageLimit(3);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


            }

            @Override
            public void onPageSelected(int position) {

                if (position == 0) {
                    mf.createSnackBar();
                    if(MyApplication.mLoadSharkTrackFlag)
                    {
                        MyApplication.mLoadSharkTrackFlag = false;
                        mf.createSharkTrack(MyApplication.mCurrentSharkSelected);
                    }
                }
                else
                    mf.closeSnackBar();

                if (position == 1) {
                    sf.update();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mViewPager.setAdapter(mSectionsPagerAdapter);



        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_tabbed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.help_button) {
            openHelpDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            if(position == 0) // if the position is 0 we are returning the First tab
            {
                Log.d("MainTabbed", "Here");
                mf = new MapFragment();
                return mf;
            }
            if(position == 1)
            {
                sf = new SharkFragment();
                return sf;
            }
            if(position == 2)
            {
                return new ListFragment();
            }
            if(position == 3)
            {
                return new SponsorFragment();
            }


            return null;
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Map";
                case 1:
                    return "Detail";
                case 2:
                    return "Animal List";
                case 3:
                    return "Sponsors";
            }
            return null;
        }



    }

    private void openHelpDialog() {
        final MaterialDialog mMaterialDialog = new MaterialDialog(this);
        View view = View.inflate(this, R.layout.map_help_dialog, null);
        mMaterialDialog.setTitle("Map Help");
        mMaterialDialog.setContentView(view);
        mMaterialDialog.setPositiveButton("Accept", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMaterialDialog.dismiss();
            }
        });
        mMaterialDialog.show();
    }


}
