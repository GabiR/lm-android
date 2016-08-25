package com.cypien.leroy.models;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by Alex on 8/4/2015.
 */
public class Store implements Serializable{

    private String name;
    private String address;
    private String open;
    private double latitude;
    private double longitude;
    private String phone;
    private String directions;
    private String facebookAddress;

    public Store (double latitude,double longitude){
        this.latitude=latitude;
        this.longitude=longitude;
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

    public String getFacebookAddress() {
        return facebookAddress;
    }

    public void setFacebookAddress(String facebookAddress) {
        this.facebookAddress = facebookAddress;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDirections() {
        return directions;
    }

    public void setDirections(String directions) {
        this.directions = directions;
    }

    public LatLng getPosition() {
        return new LatLng(latitude,longitude);
    }
}
