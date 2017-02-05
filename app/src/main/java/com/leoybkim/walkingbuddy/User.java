package com.leoybkim.walkingbuddy;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by riesgo on 04/02/2017.
 */

public class User implements Parcelable {
    private String fbName;
    private Drawable userPic;
    private String userFbID; // info needed for the messenger button, whatever it is.
    private LatLng src;
    private LatLng dest;
    private User buddy;
    private boolean [] matching;

    public User(String fbName, Drawable userPic, String userFbID, LatLng src,
                LatLng dest, @Nullable User buddy) {
        this.fbName = fbName;
        this.userPic = userPic;
        this.userFbID = userFbID;
        this.src = src;
        this.dest = dest;
        this.buddy = buddy;
        this.matching = new boolean[]{false, false};
    }

    // Functions

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(fbName);
        parcel.writeParcelable((Parcelable) userPic, i);
        parcel.writeString(userFbID);
        parcel.writeParcelable(src, i);
        parcel.writeParcelable(dest, i);
        parcel.writeParcelable(buddy, i);
        parcel.writeBooleanArray(matching);
    }

    // Getters and setters

    public String getFbName() {
        return fbName;
    }

    public void setFbName(String fbName) {
        this.fbName = fbName;
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

    public boolean [] getMatching() {
        return matching;
    }

    public void setMatching(boolean [] matching) {
        this.matching = matching;
    }

}
