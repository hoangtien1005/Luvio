package com.android.Luvio.activities.Main;

import java.io.Serializable;

public class Photo implements  Serializable{
    private int rscID;

    public Photo(int rscID) {
        this.rscID = rscID;
    }
    public int getRsc(){
        return rscID;
    }

    public void setRsc(int rscID) {
        this.rscID = rscID;
    }
}
