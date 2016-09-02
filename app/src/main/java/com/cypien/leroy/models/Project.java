package com.cypien.leroy.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Alex on 21/10/15.
 */

public class Project implements Serializable{

    private String blogid;
    private String title;
    private transient Bitmap image;
    private String imageBase;
    private String date;
    private String costs;
    private String duration;
    private String comments;
    private String description;
    private String views;
    private String rating;
    private boolean liked;
    private String type;
    private String username;
    private String  avatarBase;
    private transient Bitmap avatar;

    public Project(){

    }

    public Project fromJson(JSONObject jsn) {
        try {
            this.type = jsn.getString("type");
            Date dt;
            if(isBlog()){
                this.blogid=jsn.getString("blogid");
                this.costs = jsn.getString("costs");
                this.duration = jsn.getString("duration");
                this.comments = jsn.getString("comments_visible");
                this.description = jsn.getString("description").replaceAll("\\[.*\\]", "");
                dt = new Date(Long.parseLong(jsn.getString("dateline")+"000"));
                this.title=jsn.getString("title");
                this.views = jsn.getString("views");
            }else{
                this.blogid=jsn.getString("nodeid");
                this.comments = jsn.getString("replycount");
                this.description = jsn.getString("pagetext").replaceAll("\\[.*\\]", "");
                dt = new Date(Long.parseLong(jsn.getString("publishdate")+"000"));
                this.title=jsn.getString("title").replace("Articol: ", "");
                this.views = jsn.getString("viewcount");
            }
            this.rating = jsn.getString("likenum");
            this.imageBase=jsn.getString("mainimage");
            this.username = jsn.getString("username");
            this.avatarBase = jsn.getString("avatar");
            SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            this.date = df.format(dt);
            if(jsn.getInt("isliked")==0){
                liked=false;
            }else
                liked=true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public boolean isLiked() {
        return liked;
    }

  public Bitmap getAvatar() {
    return avatar;
  }

  public String getUsername() {
    return username;
  }

  public String getBlogid() {
        return blogid;
    }

    public String getTitle() {
        return title;
    }

    public Bitmap getImage() {
        return image;
    }

    public String getDate() {
        return date;
    }

    public String getCosts() {
        return costs;
    }

    public String getDuration() {
        return duration;
    }

    public String getComments() {
        return comments;
    }

    public String getDescription() {
        return description;
    }

    public String getViews() {
        return views;
    }

    public String getRating() {
        return rating;
    }

    public boolean isBlog(){
        return this.type.equals("blog");
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    // transforma un string base64 in imagine
    public static Bitmap decodeBase64(String input){
        byte[] decodedByte = Base64.decode(input, 0);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length, options);
        options.inSampleSize = calculateInSampleSize(options, 420, 330);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length,options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public String getImageBase() {
        return imageBase;
    }

    public void setImageBase(String imageBase) {
        this.imageBase = imageBase;
    }

    public void buildImage(){
        image=decodeBase64(imageBase);
        //       imageBase=null;
    }

    public void buildAvatar(){
        avatar = decodeBase64(avatarBase);
    }
}