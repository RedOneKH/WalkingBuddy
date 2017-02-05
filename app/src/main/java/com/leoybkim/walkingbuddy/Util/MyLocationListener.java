package com.leoybkim.walkingbuddy.Util;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.LocationListener;
import com.leoybkim.walkingbuddy.LoginActivity;

/**
 * Created by Dan on 17/11/2016.
 */

public class MyLocationListener implements LocationListener {
    @Override
    public void onLocationChanged(Location location) {
        Log.i("LocationListener", "Location Changed to " + location.toString());
        LoginActivity.setmLocation(location);
    }
}
