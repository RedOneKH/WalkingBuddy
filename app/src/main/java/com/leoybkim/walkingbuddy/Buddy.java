package com.leoybkim.walkingbuddy;

import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;

/**
 * Created by riesgo on 04/02/2017.
 */

public class Buddy {
    private Drawable buddyPic;
    private String name;
    private String buddyid;

    public Buddy (String buddyid, @Nullable String name, @Nullable Drawable buddyPic) {
        this.buddyid = buddyid;
        this.name = name;
        this.buddyPic = buddyPic;
    }

    public Drawable getBuddyPic() {
        return buddyPic;
    }

    public void setBuddyPic(Drawable buddyPic) {
        this.buddyPic = buddyPic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBuddyid() {
        return buddyid;
    }

    public void setBuddyid(String buddyid) {
        this.buddyid = buddyid;
    }



}
