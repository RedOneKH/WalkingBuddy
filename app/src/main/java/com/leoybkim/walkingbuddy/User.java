package com.leoybkim.walkingbuddy;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by riesgo on 04/02/2017.
 */

public class User {
    private String userID;
    private Drawable userPic;
    private String userFbID; // info needed for the messenger button, whatever it is.
    private LatLng src;
    private LatLng dest;
    private User buddy;

    private boolean matched;

    public User(String userID, Drawable userPic, String userFbID, LatLng src,
                LatLng dest, @Nullable User buddy) {
        this.userID = userID;
        this.userPic = userPic;
        this.userFbID = userFbID;
        this.src = src;
        this.dest = dest;
        this.buddy = buddy;
        matched = false;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Drawable getUserPic() {
        return userPic;
    }

    public void setUserPic(Drawable userPic) {
        this.userPic = userPic;
    }

    public String getUserFbID() {
        return userFbID;
    }

    public void setUserFbID(String userFbID) {
        this.userFbID = userFbID;
    }

    public LatLng getSrc() {
        return src;
    }

    public void setSrc(LatLng src) {
        this.src = src;
    }

    public LatLng getDest() {
        return dest;
    }

    public void setDest(LatLng dest) {
        this.dest = dest;
    }

    public User getBuddy() {
        return buddy;
    }

    public void setBuddy(User buddy) {
        this.buddy = buddy;
    }

    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }

}
