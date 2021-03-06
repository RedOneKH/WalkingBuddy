package com.leoybkim.walkingbuddy.BuddyMatcher;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.ContentFrameLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.leoybkim.walkingbuddy.R;
import com.leoybkim.walkingbuddy.User;

import java.util.ArrayList;


/**
 * Created by dmedinag on 04/02/2017.
 */

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {
    private static ArrayList<User> mDataset;
    private static Context mContext;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mName;
        public ImageView mProfilePic;
        public Button mContact;

        public ViewHolder(View v) {
            super(v);
            mName = (TextView) v.findViewById(R.id.buddyName);
            mProfilePic = (ImageView) v.findViewById(R.id.buddyPic);
            mContact = (Button) v.findViewById(R.id.fbm_button);
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openMessenger();
                }
            };
            mContact.setOnClickListener(listener);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public CardAdapter(ArrayList<User> dataset, Context context) {
        mDataset = dataset;
        mContext = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.buddy_card, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mName.setText(mDataset.get(position).getFbName());
    }

    public static void openMessenger() {
        String url = "https://facebook.com/messages/t/" + mDataset.get(0).getUserFbID();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        mContext.startActivity(i);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}

