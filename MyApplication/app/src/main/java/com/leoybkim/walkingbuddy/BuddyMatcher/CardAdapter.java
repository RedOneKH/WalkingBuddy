package com.leoybkim.walkingbuddy.BuddyMatcher;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;


/**
 * Created by dmedinag on 04/02/2017.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.MyViewHolder> {

    private Context mContext;
    String name, phone;
    double price;
    public Switch toggle;
    public String key;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, name, phone, money;
        public ImageView nameIcon, phoneIcon, moneyIcon;

        public MyViewHolder(View view) {
            super(view);

            title = (TextView) view.findViewById(R.id.title);

            name = (TextView) view.findViewById(R.id.name);
            nameIcon = (ImageView) view.findViewById(R.id.nameIcon);

            money = (TextView) view.findViewById(R.id.price);
            moneyIcon = (ImageView) view.findViewById(R.id.priceIcon);

            phone = (TextView) view.findViewById(R.id.phone);
            phoneIcon = (ImageView) view.findViewById(R.id.phoneIcon);

            toggle = (Switch) view.findViewById(R.id.toggle_switch);

        }
    }


    public CardAdapter(Context mContext, List<Spot> spotList, String name, String phone, double price) {
        this.mContext = mContext;
        this.spotList = spotList;
        this.name = name;
        this.phone = phone;
        this.price = price;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_item, parent, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        holder.phone.setText(phone);
        holder.name.setText(name);
        holder.money.setText(Double.toString(price));
        holder.title.setText(Double.toString(spot.getLat()) + ", " + Double.toString(spot.getLng()));
        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean bool = spotList.get(position).getOpen();
                database.child(key);
                User u = user;
                Spot s = u.getSpots().get(position);
                s.setOpen(!bool);
                database.getRef().setValue(u);

                firebase.getRef().child("Open").setValue(!bool);


            }
        });
    }


    @Override
    public int getItemCount() {
        return spotList.size();
    }
}
