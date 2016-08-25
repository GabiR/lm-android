package com.cypien.leroy.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;

import com.cypien.leroy.LeroyApplication;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by GabiRotaru on 24/08/16.
 */
public class CommunityMainActivity extends Activity {

    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LeroyApplication application = (LeroyApplication) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Screen:" + "MainActivity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        sp = getSharedPreferences("com.cypien.leroy_preferences", MODE_PRIVATE);
        if(sp.getBoolean("isConnected",false)){
            getUserInformation();
        }
    }

    //ia de pe server informatiile despre utilizator
    private void getUserInformation(){
        try {
            JSONObject response = ((LeroyApplication)getApplication()). makeRequest("user_get",sp.getString("userid", ""));
            response = response.getJSONObject("result");
            spEditor = sp.edit();
            spEditor.putString("email", response.getString("email"));
            spEditor.putString("birthday", response.getString("birthday"));
            spEditor.putString("firstname", response.getString("firstname"));
            spEditor.putString("lastname", response.getString("lastname"));
            spEditor.putString("phone", response.getString("phone"));
            spEditor.putString("avatar", response.getString("avatar"));
            spEditor.putString("address", response.getString("address"));
            spEditor.putString("city", response.getString("city"));
            spEditor.putString("posts", response.getString("posts"));
            spEditor.putString("dailyposts", "" + (response.getDouble("dailyposts") % 2f));
            spEditor.putString("friendcount", response.getString("friendcount"));
            spEditor.putString("joindate", getDate(Long.parseLong(response.getString("joindate") + "000")));
            spEditor.putString("profilevisits", response.getString("profilevisits"));
            spEditor.putString("blognum", response.getString("blognum"));
            spEditor.commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // transforma timpul din milisecunde in data calendaristica
    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd.MM.yyyy", cal).toString();
        return date;
    }
}
