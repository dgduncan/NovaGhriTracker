package com.upperz.sharktracker;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.upperz.sharktracker.Classes.Animal;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

import co.dift.ui.SwipeToAction;
import de.hdodenhof.circleimageview.CircleImageView;

public class SharkAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Animal> items;



    public class WorkoutViewHolder extends SwipeToAction.ViewHolder<Animal>
    {
        public CircleImageView mProfilePic;
        public TextView timeSinceUpdatedView;
        public TextView sharkView;

        public WorkoutViewHolder(View v) {
            super(v);

            mProfilePic = (CircleImageView) v.findViewById(R.id.profile_image);
            timeSinceUpdatedView = (TextView) v.findViewById(R.id.timeSinceUpdated);
            sharkView = (TextView) v.findViewById(R.id.sharkTextView);

        }
    }

    public SharkAdapter(List<Animal> items)
    {
        this.items = items;
    }

    @Override
    public int getItemViewType(int position)
    {
        return 0;
    }

    @Override
    public int getItemCount()
    {
        return items.size();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_view, parent, false);

        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Animal item = items.get(position);

        DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy");
        DateTime dt = formatter.parseDateTime(item.date);

        int recenttest = (Days.daysBetween(dt, new LocalDateTime().toDateTime()).getDays());

        WorkoutViewHolder vh = (WorkoutViewHolder) holder;

        vh.timeSinceUpdatedView.setText(String.valueOf(recenttest) + " Days Ago");
        vh.sharkView.setText(item.name);
        loadApproptiateImage(vh, item);
        vh.data = item;
    }

    public void loadApproptiateImage(WorkoutViewHolder x, Animal item)
    {
        switch(item.common_name)
        {
            case "Blue Marlin":
                x.mProfilePic.setImageResource(R.drawable.whitemarlin_1);
                break;
            case "Mako Shark":
                x.mProfilePic.setImageResource(R.drawable.mako_awesome_image);
                break;
            case "enpmakosharks":
                x.mProfilePic.setImageResource(R.drawable.mako_mexico);
                break;
            case "makosharks":
                x.mProfilePic.setImageResource(R.drawable.mako_mexico);
                break;
            case "makosharksmexico":
                x.mProfilePic.setImageResource(R.drawable.mako_mexico);
                break;
            case "Oceanic Whitetip Shark":
                x.mProfilePic.setImageResource(R.drawable.owt_orig);
                break;
            case "sailfish":
                x.mProfilePic.setImageResource(R.drawable.sailfish3);
                break;
            case "Sand Tiger Shark":
                x.mProfilePic.setImageResource(R.drawable.tiger_image);
                break;
            case "Tiger Shark":
                x.mProfilePic.setImageResource(R.drawable.tiger_image);
                break;
            case "tigerbermuda2010":
                x.mProfilePic.setImageResource(R.drawable.tiger_image);
                break;
            case "tigerbermuda2011_14":
                x.mProfilePic.setImageResource(R.drawable.tiger_image);
                break;
            case "tigergrandbahama":
                x.mProfilePic.setImageResource(R.drawable.tiger_image);
                break;
            case "tigergrandcayman":
                x.mProfilePic.setImageResource(R.drawable.tiger_image);
                break;
            case "tigerwesternaustralia":
                x.mProfilePic.setImageResource(R.drawable.tiger_image);
                break;
            case "White Marlin":
                x.mProfilePic.setImageResource(R.drawable.whitemarlin_1);
                break;
            case "islamakorace":
                x.mProfilePic.setImageResource(R.drawable.mako_awesome_image);
                break;
            default:
                x.mProfilePic.setImageResource(R.drawable.whitemarlin_1);

        }
    }


}
