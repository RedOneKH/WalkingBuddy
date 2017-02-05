package com.leoybkim.walkingbuddy.BuddyMatcher;

import android.app.IntentService;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.leoybkim.walkingbuddy.User;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

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

    public ArrayList<User> matches(ArrayList<User> users)  {

        ArrayList<User> matches = new ArrayList<>();
        L1: for( User other : users ) {
            if( other.getMatching()[0] || other.getUserFbID() == mUser.getUserFbID() ) continue;
            if( ! isClose( mUser.getSrc(), other.getSrc() ) ) continue;
            if( ! isClose( mUser.getDest(), other.getDest() ) ) continue;
            // We found a match!
            matches.add(other);
            // If this is the last user creating the FoundBuddy, mark the everyone as done

            // TODO: When there's more than one match possible (departure time) skip the coming
            // lines until there's only 5 minutes left for the trip (consider the trip is "closed")
            for( User match : matches ) {
                if( ! match.getMatching()[1] ) break L1;
            }
            // We are he last ones! Set everyone to matched
            for( User match : matches ) {
                match.setMatching(0, true);
            }
        }
        mUser.setMatching(1, true);

        return matches;
    }


    @Override
    protected void onHandleIntent(Intent workIntent) {
        // Gets data from the incoming Intent
        mUser = workIntent.getParcelableExtra("user");
        // Access the database
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("pendingUsers");
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Do work here, based on the contents of bundle
                    // create an ArrayList to store potential matches
                ArrayList<User> elems = new ArrayList<>();
                    // fill the Arraylist with the elements in the database
                for (DataSnapshot user : dataSnapshot.getChildren()) {
                    elems.add( (User) user.getValue());
                }
                    // filter only the matches
                ArrayList<User> matches = matches(elems);
                    // launch a FoundBuddyActivity when there are some matches
                if( ! matches.isEmpty() ) {
                    // Launch the activity !
                    Intent intent = new Intent(MatchFinderService.this, BuddyFoundActivity.class);
                    intent.putExtra("users", matches);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        };
        database.addValueEventListener(postListener);
    }

}
