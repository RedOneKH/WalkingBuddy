package com.leoybkim.walkingbuddy.Util;

import android.os.Bundle;
import android.support.v4.media.session.IMediaControllerCallback;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;
import com.leoybkim.walkingbuddy.R;

/**
 * Created by Dan on 04/02/2017.
 */

public class DestinationActivity extends AppCompatActivity {

    private ImageView imageView;
    private Button confirm;

    //location of destination
    private LatLng destination, origin;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.destination_confirmation_activity);

        imageView = (ImageView) findViewById(R.id.directionsImage);
        confirm = (Button) findViewById(R.id.confirmDirectionsButton);

        destination = (LatLng) savedInstanceState.get("destination");
        origin = (LatLng) savedInstanceState.get("origin");


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