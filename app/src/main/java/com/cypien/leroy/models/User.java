package com.cypien.leroy.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.cypien.leroy.LeroyApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Alex on 21/10/15.
 */
public class User implements Serializable{

    private String userName;
    private transient Bitmap avatar;
    private String avatarBase;

    public User (String userid){
        JSONObject jsn = LeroyApplication.getInstance().makeRequest("user_get",userid);
        try {
            jsn = jsn.getJSONObject("result");
            this.userName = jsn.getString("username");
            this.avatarBase = jsn.getString("avatar");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getUserName() {
        return userName;
    }

    public Bitmap getAvatar() {
        return avatar;
    }

    // transforma un string base64 in imagine
    public static Bitmap decodeBase64(String input){
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    public void buildImage(){
        avatar=decodeBase64(avatarBase);
    }
}
