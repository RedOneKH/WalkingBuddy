package com.leoybkim.walkingbuddy;

import android.app.IntentService;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.leoybkim.walkingbuddy.BuddyMatcher.MatchFinderService;

/**
 * Created by dmedinag on 05/02/2017.
 */

public class LookingForBuddyActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.looking_for_buddy);
        IntentService bg = new MatchFinderService();
        bg.startActivity( getIntent() );

    }

}
