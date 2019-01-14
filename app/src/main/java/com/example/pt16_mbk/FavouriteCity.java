package com.example.pt16_mbk;

import android.app.Activity;
import android.content.SharedPreferences;

public class FavouriteCity {

    SharedPreferences prefs;

    public FavouriteCity(Activity activity){
        prefs = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    String getCity(){
        return prefs.getString("city", "Sydney, AU");
    }

    void setCity(String city){
        prefs.edit().putString("city", city).commit();
    }
}
