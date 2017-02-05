package com.leoybkim.walkingbuddy.BuddyMatcher;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.leoybkim.walkingbuddy.User;

import java.util.ArrayList;

/**
 * Created by dmedinag on 04/02/2017.
 */

public class MatchFinderService extends IntentService {

    User mUser;

    public MatchFinderService() {
        super("MatchFinderService");
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

    public boolean isAMatch(User[] users)  {

        ArrayList<User> matches = new ArrayList<User>();
        L1: for( User other : users ) {
            if( other.isMatched() ) continue;
            if( ! isClose( mUser.getSrc(), other.getSrc() ) ) continue;
            if( ! isClose( mUser.getDest(), other.getDest() ) ) continue;
            // We found a match!
            matches.add(other);
            // If this is the last user creating the FoundBuddy, mark the everyone as done
            for( User match : matches ) {
                if( ! match.isDone() ) break L1;
            }
            // We are he last ones! Set everyone to matched
            for( User match : matches ) {
                match.setMatched(true);
            }
        }
        mUser.setDone(true);

        return true;
    }


    @Override
    protected void onHandleIntent(Intent workIntent) {
        // Gets data from the incoming Intent
        Parcelable bundle = workIntent.getParcelableExtra("user");

        // Do work here, based on the contents of bundle

//        LatLng source1  = new LatLng( bundle.getDouble("src1lat"), bundle.getDouble("src1lng") );
//        LatLng dest1    = new LatLng( bundle.getDouble("dst1lat"), bundle.getDouble("dst1lng") );
//        LatLng source2  = new LatLng( bundle.getDouble("src2lat"), bundle.getDouble("src2lng") );
//        LatLng dest2    = new LatLng( bundle.getDouble("dst2lat"), bundle.getDouble("dst2lng") );
//
//        SourceDestPair user1 = new SourceDestPair( source1, dest1 );
//        SourceDestPair user2 = new SourceDestPair( source2, dest2 );
//
//        if( isAMatch(user1, user2) ) {
//            // Launch the activity !
//            Intent intent = new Intent(MatchFinderService.this, BuddyFoundActivity.class);
//            intent.putExtra("id", bundle.getString("userID"));
//            startActivity(intent);
//        }
    }



}
