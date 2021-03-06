package com.leoybkim.walkingbuddy;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.leoybkim.walkingbuddy.BuddyMatcher.MatchFinderService;

/**
 * Created by dmedinag on 05/02/2017.
 */

public class LookingForBuddyActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LoginActivity.class.getSimpleName(), "Trying to create LFBA");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_looking_for_buddy);
        IntentService bg = new MatchFinderService();
        Intent intent = new Intent(getApplicationContext(), MatchFinderService.class);
        intent.putExtra("user", getIntent().getExtras().getParcelable("user"));
//        bg.startActivity( intent );
        getApplicationContext().startService(intent);


    }

}
