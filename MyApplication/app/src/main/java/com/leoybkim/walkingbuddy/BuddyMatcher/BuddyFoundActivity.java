package com.leoybkim.walkingbuddy.BuddyMatcher;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.leoybkim.walkingbuddy.R;

/**
 * Created by dmedinag on 04/02/2017.
 */

public class BuddyFoundActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // call the super class onCreate to complete the creation of
        // activity like the view hierarchy
        super.onCreate(savedInstanceState);

        // set the user interface layout for this Activity
        // the layout file is defined in the project res/layout/buddy_found.xml file
        setContentView(R.layout.buddy_found);

        // Instantiate the object in the view
        RecyclerView recycler = findViewById(R.id.recycler_view);

        recycler.a

    }
//    TODO: listeners

}
