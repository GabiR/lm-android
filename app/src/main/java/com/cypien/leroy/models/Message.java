package com.cypien.leroy.models;/*
 * Created by Alex on 26.09.2016.
 */

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.UUID;

public class Message implements Serializable {
    String id;
    String title;
    String content;
    String date;
    boolean read;

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public Message() {
        this.id = UUID.randomUUID().toString();
        this.read = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }




    public String toJson() {
        JSONObject jsn = new JSONObject();

        try {

            jsn.put("title", title);
            jsn.put("content", content);
            jsn.put("read", Boolean.valueOf(read));
            jsn.put("date", date);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsn.toString();
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Message fromJson(JSONObject jsn) {

        try {

            this.title = jsn.getString("title");
            this.content = jsn.getString("content");
            this.read = Boolean.parseBoolean(jsn.getString("read"));
            this.date = jsn.getString("date");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }
}
