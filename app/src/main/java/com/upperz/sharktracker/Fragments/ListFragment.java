package com.upperz.sharktracker.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.ParseObject;
import com.upperz.sharktracker.R;
import com.upperz.sharktracker.SharkAdapter;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Comparator;


public class ListFragment extends Fragment
{

    int recenttest1;
    int recenttest2;

    public String trackName;

    DateTime dt1;
    DateTime dt2;
    DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy");



    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        SharkAdapter adapter;
        RecyclerView recyclerView;

        View v =inflater.inflate(R.layout.fragment_animal_list,container,false);

        /*


        recyclerView = (RecyclerView) v.findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                try {
                    TextView t = (TextView) view.findViewById(R.id.sharkTextView);
                    MyApplication.mCurrentSharkSelected = t.getText().toString();

                    Intent intent = new Intent(getActivity(), SharkActivity.class);
                    intent.putExtra("name", t.getText().toString());
                    startActivityForResult(intent, 1);



                } catch (NullPointerException e) {
                    Log.d("ListFragment", "NullPointerException Occurred");
                }
            }
        }));


        Collections.sort(MyApplication.sharks, new CustomComparator());


        adapter = new SharkAdapter(MyApplication.sharks);
        recyclerView.setAdapter(adapter);



*/
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                trackName = data.getStringExtra("trackName");
                ViewPager vp = (ViewPager) getActivity().findViewById(R.id.container);
                TabLayout tl = (TabLayout) getActivity().findViewById(R.id.tabs);
                vp.setCurrentItem(0);
                tl.setScrollPosition(0, 0, true);
            }
        }
    }

    public class CustomComparator implements Comparator<ParseObject> {
        @Override
        public int compare(ParseObject o1, ParseObject o2)
        {

            dt1 = formatter.parseDateTime(o1.getString("date"));
            dt2 = formatter.parseDateTime(o2.getString("date"));

            recenttest1 = (Days.daysBetween(dt1, new LocalDateTime().toDateTime()).getDays());
            recenttest2 = (Days.daysBetween(dt2, new LocalDateTime().toDateTime()).getDays());

            return recenttest1 - recenttest2;
        }
    }

}
