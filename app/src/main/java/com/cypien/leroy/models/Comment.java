package com.cypien.leroy.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Alex on 29/10/15.
 */
public class Comment {

    private User user;
    private String blogtextid;
    private String bloguserid;
    private String date;
    private String pagetext;
    private String rating;
    private String userid;
    private boolean liked;
    private String type;

    public Comment fromJson(JSONObject jsn, String ssh) {
        try {
            if(type.equals("project")){
                this.blogtextid = jsn.getString("blogtextid");
                this.bloguserid = jsn.getString("bloguserid");
                this.rating = ""+jsn.getInt("likenum");
                liked = jsn.getInt("isliked") != 0;
            }else{
                this.blogtextid = jsn.getString("postid");
            }
            this.userid= jsn.getString("userid");
            this.user= new User(jsn.getString("userid"), ssh);
            Date dt = new Date(Long.parseLong(jsn.getString("dateline")+"000"));
            SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            this.date = df.format(dt);
            this.pagetext = jsn.getString("pagetext").replaceAll("\\[.*\\]", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public String getUserid() {
        return userid;
    }

    public String getRating() {
        return rating;
    }

    public boolean isLiked() {
        return liked;
    }

    public User getUser() {
        return user;
    }

    public String getBlogtextid() {
        return blogtextid;
    }

    public String getBloguserid() {
        return bloguserid;
    }

    public String getDate() {
        return date;
    }

    public String getPagetext() {
        return pagetext;
    }

    public void setType(String type){
        this.type=type;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }
}
