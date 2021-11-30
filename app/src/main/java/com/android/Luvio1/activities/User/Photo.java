package com.android.Luvio1.activities.User;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Photo implements  Serializable{
    private Bitmap bitmap;

    public Photo(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
    public Bitmap getRsc(){
        return bitmap;
    }


}
