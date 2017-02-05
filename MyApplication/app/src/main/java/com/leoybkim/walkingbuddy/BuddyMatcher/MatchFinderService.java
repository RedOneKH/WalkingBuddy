package com.leoybkim.walkingbuddy.BuddyMatcher;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by dmedinag on 04/02/2017.
 */

public class MatchFinderService extends IntentService {

    public MatchFinderService() {
        super("MatchFinderService");
    }

    public class SourceDestPair{
        LatLng source;
        LatLng dest;
        public SourceDestPair( LatLng source, LatLng dest) {
            this.source = source;
            this.dest = dest;
        }
    }

    private boolean isClose(LatLng a, LatLng b) {
        double definedRadius = 300;
        double EARTH_RADIUS = 6371e3; // metres

        // See http://www.movable-type.co.uk/scripts/latlong.html for reference
        double psi1 = Math.toRadians(a.latitude);
        double psi2 = Math.toRadians(b.latitude);
        double psiInc = Math.toRadians(b.latitude-a.latitude);
        double lambdaInc = Math.toRadians(b.longitude-a.longitude);

        double x = (Math.sin(psiInc / 2) * Math.sin(psiInc / 2)) +
                (Math.cos(psi1) * Math.cos(psi2) * Math.sin(lambdaInc / 2) * Math.sin(lambdaInc / 2));
        double y = 2 * Math.atan2(Math.sqrt(x), Math.sqrt(1-x));

        double d = EARTH_RADIUS * y;

        return d < definedRadius;
    }

    public boolean isAMatch(SourceDestPair a, SourceDestPair b)  {
        // Compare the two origins
        if( ! isClose( a.source, b.source ) ) return false;
        if( ! isClose( a.dest, b.dest ) ) return false;
        return true;
    }


    @Override
    protected void onHandleIntent(Intent workIntent) {
        // Gets data from the incoming Intent
        Bundle bundle = workIntent.getBundleExtra("coordinates");

        // Do work here, based on the contents of bundle

        LatLng source1  = new LatLng( bundle.getDouble("src1lat"), bundle.getDouble("src1lng") );
        LatLng dest1    = new LatLng( bundle.getDouble("dst1lat"), bundle.getDouble("dst1lng") );
        LatLng source2  = new LatLng( bundle.getDouble("src2lat"), bundle.getDouble("src2lng") );
        LatLng dest2    = new LatLng( bundle.getDouble("dst2lat"), bundle.getDouble("dst2lng") );

        SourceDestPair user1 = new SourceDestPair( source1, dest1 );
        SourceDestPair user2 = new SourceDestPair( source2, dest2 );

        if( isAMatch(user1, user2) ) {
            // Launch the activity !
            Intent intent = new Intent(MatchFinderService.this, BuddyFoundActivity.class);
            intent.putExtra("id", bundle.getString("userID"));
            startActivity(intent);
        }
    }



}
