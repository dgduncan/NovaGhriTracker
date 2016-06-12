package com.upperz.sharktracker.Activities;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.upperz.sharktracker.Fragments.ListFragment;
import com.upperz.sharktracker.Fragments.MapFragment;
import com.upperz.sharktracker.Fragments.SponsorFragment;
import com.upperz.sharktracker.R;

import java.util.ArrayList;

import me.drakeet.materialdialog.MaterialDialog;

public class NavigationTabBar extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    public MapFragment mf;
    public ListFragment lf;
    public SponsorFragment sf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_navigation_tab_bar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        final String[] colors = getResources().getStringArray(R.array.default_preview);

        final com.gigamole.library.ntb.NavigationTabBar navigationTabBar = (com.gigamole.library.ntb.NavigationTabBar) findViewById(R.id.ntb_horizontal);
        final ArrayList<com.gigamole.library.ntb.NavigationTabBar.Model> models = new ArrayList<>();

        models.add(
                new com.gigamole.library.ntb.NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_first),
                        Color.parseColor(colors[0]))
                        .title("Heart")
                        .build()
        );
        models.add(
                new com.gigamole.library.ntb.NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_second),
                        Color.parseColor(colors[1]))
                        .title("Cup")
                        .build()
        );
        models.add(
                new com.gigamole.library.ntb.NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_third),
                        Color.parseColor(colors[2]))
                        .title("Diploma")
                        .build()
        );


        navigationTabBar.setModels(models);
        navigationTabBar.setViewPager(mViewPager, 0);

        //IMPORTANT: ENABLE SCROLL BEHAVIOUR IN COORDINATOR LAYOUT
        navigationTabBar.setBehaviorEnabled(true);

        navigationTabBar.setOnTabBarSelectedIndexListener(new com.gigamole.library.ntb.NavigationTabBar.OnTabBarSelectedIndexListener() {
            @Override
            public void onStartTabSelected(final com.gigamole.library.ntb.NavigationTabBar.Model model, final int index) {

            }

            @Override
            public void onEndTabSelected(final com.gigamole.library.ntb.NavigationTabBar.Model model, final int index) {
                model.hideBadge();
            }
        });

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
                mf = new MapFragment();
                return mf;
            }
            if(position == 1)
            {
                lf = new ListFragment();
                return lf;
            }
            if(position == 2)
            {
                sf = new SponsorFragment();
                return sf;
            }


            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Map";
                case 1:
                    return "Animal List";
                case 2:
                    return "Sponsors";
            }
            return null;
        }



    }


    /***
     * Function for opening the help dialog when the menu button is pressed
     */
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

        if(id == R.id.about_button)
        {
            AlertDialog.Builder ad = new AlertDialog.Builder(this);
            ad.setTitle("About this Application");
            ad.setView(LayoutInflater.from(this).inflate(R.layout.dialog_about, null));
            ad.setPositiveButton("OK", new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                }
            });
            ad.show();

        }

        return super.onOptionsItemSelected(item);
    }
}
