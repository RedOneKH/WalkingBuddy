package com.leoybkim.walkingbuddy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.leoybkim.walkingbuddy.LoginActivity;
import com.leoybkim.walkingbuddy.LookingForBuddyActivity;
import com.leoybkim.walkingbuddy.R;
import com.leoybkim.walkingbuddy.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Dan on 04/02/2017.
 */

public class DestinationActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private ImageView imageView;
    private Button confirm;
    //location of destination
    private User mUser;
    private DatabaseReference mDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Bundle userInfoState = getIntent().getExtras();
        if (userInfoState != null) {
            mUser = userInfoState.getParcelable("user");
            Log.d(TAG, "DestinationActivity received user information");
        } else {
            mUser = null;
            Log.d(TAG, "An error occurred: no user arrived to DestinationActivity");
        }

        // Link layout to activity
        setContentView(R.layout.destination_confirmation_activity);

        // Get views from the layout
        imageView = (ImageView) findViewById(R.id.directionsImage);
        confirm = (Button) findViewById(R.id.confirmDirectionsButton);


        // Measure screen dimensions
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;

        loadImage(width, height);

        // Get instance to the database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Add listener to click on confirm button
        confirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                // write user into database
                writeNewUser(mUser);

                // create intent to switch to LookingForBuddyActivity
                Intent intent = new Intent(getApplicationContext(), LookingForBuddyActivity.class);
                intent.putExtra("user", mUser);
                // start activity
                startActivity(intent);
            }
        });
    }

    private void writeNewUser(User user) {
        // Add user to the database
        ArrayList<Object> userList = new ArrayList<>();
        userList.add(user.getFbName());
        userList.add(user.getUserPic());
        userList.add(user.getUserFbID());
        userList.add(user.getSrc());
        userList.add(user.getDest());
        userList.add(user.getBuddy());

        mDatabase.child("pendingUsers")
                .child(user.getUserFbID())
                .setValue(userList);
    }

    private void loadImage(final int width, final int height) {
        imageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Picasso.with(getApplicationContext())
                        .load(getStaticMapURL(mUser.getSrc(), mUser.getDest(), width, height))
                        .into(imageView);
            }
        });

    }

    private String getStaticMapURL(LatLng src, LatLng dest, int width, int height){
        return String.format("https://maps.googleapis.com/maps/api/staticmap" +
                        "?markers=color:red%%7C%f,%f" +
                        "&markers=color:green%%7C%f,%f" +
                        "&key=%s" +
                        "&size=%dx%d",
                src.latitude,
                src.longitude,
                dest.latitude,
                dest.longitude,
                getString(R.string.dan_googleMap_key),
                width,
                height);
    }


}