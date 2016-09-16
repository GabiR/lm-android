package com.cypien.leroy.models;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Alex on 8/4/2015.
 */
public class Store implements Serializable {

    private String id;
    private String name;
    private String address;
    private String open;
    private double latitude;
    private double longitude;
    private String phone;
    private String facebookAddress;

    public Store(String id, double latitude, double longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Store() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFacebookAddress() {
        return facebookAddress;
    }

    public void setFacebookAddress(String facebookAddress) {
        this.facebookAddress = facebookAddress;
    }

    public LatLng getPosition() {
        return new LatLng(latitude, longitude);
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    public String toJson() {
        JSONObject jsn = new JSONObject();

        try {

            jsn.put("name", name);
            jsn.put("address", address);
            jsn.put("open", open);
            jsn.put("latitude", latitude);
            jsn.put("longitude", longitude);
            jsn.put("phone", phone);
            jsn.put("facebookAddress", facebookAddress);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsn.toString();
    }


    public Store fromJson(JSONObject jsn) {

        try {

            this.name = jsn.getString("name");
            this.address = jsn.getString("address");
            this.open = jsn.getString("open");
            this.phone = jsn.getString("phone");
            this.facebookAddress = jsn.getString("facebookAddress");
            this.latitude = jsn.getDouble("latitude");
            this.longitude = jsn.getDouble("longitude");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
