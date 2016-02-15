package com.upperz.sharktracker.Adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.upperz.sharktracker.Classes.Animal;
import com.upperz.sharktracker.R;

public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.InfoViewHolder>
{

    Animal animal;


    public class InfoViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView title;
        TextView name;
        TextView sex;

        public InfoViewHolder(View itemView) {
            super(itemView);

            cv= (CardView)itemView.findViewById(R.id.cv);
            title = (TextView)itemView.findViewById(R.id.title);
            name = (TextView)itemView.findViewById(R.id.name);
            sex = (TextView)itemView.findViewById(R.id.sex);
        }
    }

    public InfoAdapter(Animal animal)
    {
        this.animal = animal;
    }

    @Override
    public InfoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.info_item, parent, false);

        return new InfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(InfoViewHolder holder, int position)
    {

        holder.title.setText("Information");
        holder.name.setText(animal.name);
        holder.sex.setText(animal.sex);

    }

    @Override
    public int getItemCount() {
        return 1;
    }

}
