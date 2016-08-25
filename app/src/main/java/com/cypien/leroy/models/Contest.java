package com.cypien.leroy.models;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Alex on 03/11/15.
 */
public class Contest {
    private String contestId;
    private String title;

    public Contest(){

    }

    public Contest fromJson(JSONObject jsn) {
        try {
            this.contestId = jsn.getString("nodeid");
            this.title = jsn.getString("title");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public String getContestId() {
        return contestId;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return title;
    }
}
