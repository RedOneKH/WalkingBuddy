package com.leoybkim.walkingbuddy;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.internal.ParcelableSparseArray;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.leoybkim.walkingbuddy.BuddyMatcher.CardAdapter;
import static android.content.ContentValues.TAG;

import java.util.ArrayList;

/**
 * Created by dmedinag on 04/02/2017.
 */

public class BuddyFoundActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buddy_found);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        savedInstanceState = getIntent().getExtras();
        ArrayList<Parcelable> matches = savedInstanceState.getParcelableArrayList("users");
        ArrayList<User> users = new ArrayList<>();
        for( Parcelable u : matches ) {
            users.add( (User) u);
        }
        Log.d(TAG, "users: " + users.toString());

        mAdapter = new CardAdapter( users );
        mRecyclerView.setAdapter(mAdapter);
    }

}
