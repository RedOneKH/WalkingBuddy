package com.leoybkim.walkingbuddy.Util;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.media.session.IMediaControllerCallback;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;
import com.leoybkim.walkingbuddy.LoginActivity;
import com.leoybkim.walkingbuddy.R;
import com.squareup.picasso.Picasso;

import java.io.InputStream;

/**
 * Created by Dan on 04/02/2017.
 */

public class DestinationActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private ImageView imageView;
    private Button confirm;

    //location of destination
    private LatLng destination, origin;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null){
            savedInstanceState = getIntent().getBundleExtra("bundle");
        }
        Log.d(TAG, "DestinationActivity!");
        setContentView(R.layout.destination_confirmation_activity);

        imageView = (ImageView) findViewById(R.id.directionsImage);
        confirm = (Button) findViewById(R.id.confirmDirectionsButton);

        destination = (LatLng) savedInstanceState.get("destination");
        origin = (LatLng) savedInstanceState.get("origin");

        new DownloadImageTask(imageView).execute(getStaticMapURL(origin, destination, 250, 250));
    }

    private void loadImage() {
        imageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Picasso.with(getApplicationContext())
                        .load(getStaticMapURL(origin, destination, imageView.getWidth(), imageView.getHeight()))
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

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}