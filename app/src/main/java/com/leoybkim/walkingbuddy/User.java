package com.leoybkim.walkingbuddy;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

import static android.content.ContentValues.TAG;

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
    private boolean [] matching = new boolean[]{false, false};

    public User(String fbName, @Nullable Drawable userPic, String userFbID, LatLng src,
                LatLng dest, @Nullable User buddy) {
        this.fbName = fbName;
        this.userPic = userPic;
        this.userFbID = userFbID;
        this.src = src;
        this.dest = dest;
        this.buddy = buddy;
    }

    // Functions

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(fbName);
        // TODO: drawable to string?
        parcel.writeString(userFbID);
        parcel.writeDoubleArray(new double[]{src.latitude, src.longitude});
        parcel.writeDoubleArray(new double[]{dest.latitude, dest.longitude});
        // TODO: buddy to string
        parcel.writeBooleanArray(matching);
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public User(Parcel in) {
        fbName = in.readString();

//        this.userPic = in.readString().replace('android.graphics.', 'R'); // TODO: image to str and back?

        userFbID = in.readString();

        double [] srca = new double[2];
        in.readDoubleArray(srca);
        src = new LatLng(srca[0], srca[1]);

        double [] desta = new double[2];
        in.readDoubleArray(desta);
        dest = new LatLng(desta[0], desta[1]);

//        buddy = in.readParcelable(null);

        in.readBooleanArray(matching);
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

    public void setMatching(int index, boolean matching) {
        this.matching[index] = matching;
    }

}
