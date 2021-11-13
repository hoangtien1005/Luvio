package com.android.Luvio.utilities;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    private final SharedPreferences sharedPreferences;

    public PreferenceManager(Context context){
        sharedPreferences=context.getSharedPreferences(Constants.KEY_SIGN_IN_PREFERENCE,Context.MODE_PRIVATE);
    }

    public Boolean putString(String key){
        return sharedPreferences.getBoolean(key,false);
    }

    public void putString (String key,String val){
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(key,val);
        editor.apply();
    }

    public  String getString(String key){
        return sharedPreferences.getString(key,null);

    }
    public void clear(){
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
