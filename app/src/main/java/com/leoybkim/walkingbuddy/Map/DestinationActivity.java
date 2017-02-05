package com.leoybkim.walkingbuddy.Map;

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

/**
 * Created by Dan on 04/02/2017.
 */

public class DestinationActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private ImageView imageView;
    private Button confirm;
    //location of destination
    private LatLng destination, origin;
    private Bundle userInfoState;
    private String mFBid;
    private String mFBname;
    private Bitmap mFBbitmap;
    private User mUser;
    private LatLng mOrigin;
    private LatLng mDest;
    private DatabaseReference mDatabase;



    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();


        if(savedInstanceState == null){
            savedInstanceState = getIntent().getBundleExtra("bundle");
            userInfoState = getIntent().getExtras();
            if (userInfoState != null) {
                mUser = userInfoState.getParcelable("FBinfo");

            }
        }
        Log.d(TAG, "DestinationActivity!");
        setContentView(R.layout.destination_confirmation_activity);

        imageView = (ImageView) findViewById(R.id.directionsImage);
        confirm = (Button) findViewById(R.id.confirmDirectionsButton);

        destination = (LatLng) savedInstanceState.get("destination");
        origin = (LatLng) savedInstanceState.get("origin");


        //measure screen dimensions
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;

        loadImage(width, height);

        confirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // CREATE USER ADD TO FIREBASE
                // profile, name, messenger info
                writeNewUser(mUser);
                // start activity
                Intent intent = new Intent(getApplicationContext(), LookingForBuddyActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("user", mUser);
                startActivity(intent);
            }
        });
    }

    private void writeNewUser(User user) {
        mFBid = user.getUserFbID();
//        mFBname = user.getFbName();
//        mOrigin = user.getSrc();
//        mDest = user.getDest();
//
//        JSONObject obj = new JSONObject();
//        try {
//            obj.put("name", mFBname);
//            obj.put("origin", mOrigin);
//            obj.put("dest", mDest);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
        mDatabase.child("pendingUsers").child(mFBid).setValue(user);

    }

    private void loadImage(final int width, final int height) {
        imageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Picasso.with(getApplicationContext())
                        .load(getStaticMapURL(origin, destination, width, height))
                        .into(imageView);
            }
        });

    }

    private String getStaticMapURL(LatLng origin, LatLng destination, int width, int height){
        return String.format("https://maps.googleapis.com/maps/api/staticmap" +
                        "?markers=color:red%%7C%f,%f" +
                        "&markers=color:green%%7C%f,%f" +
                        "&key=%s" +
                        "&size=%dx%d",
                origin.latitude,
                origin.longitude,
                destination.latitude,
                destination.longitude,
                getString(R.string.dan_googleMap_key),
                width,
                height);
    }


}