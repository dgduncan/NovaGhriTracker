package com.upperz.sharktracker.Activities;

import android.content.pm.ActivityInfo;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.parse.ParseAnalytics;
import com.upperz.sharktracker.Fragments.ListFragment;
import com.upperz.sharktracker.Fragments.MapFragment;
import com.upperz.sharktracker.MyApplication;
import com.upperz.sharktracker.R;
import com.upperz.sharktracker.Fragments.SharkFragment;
import com.upperz.sharktracker.Fragments.SponsorFragment;

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
        /*if (id == R.id.learn_more_button)
        {
            Intent intent = new Intent(MainTabbedActivity.this, LearnMoreActivity.class);
            startActivity(intent);
        }
*/
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main_tabbed, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
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


            return PlaceholderFragment.newInstance(position + 1);
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
