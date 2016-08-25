package com.cypien.leroy.models;

import java.io.Serializable;

/**
 * Created by alexa on 9/30/2015.
 */
public class Service implements Serializable{

    private int image;
    private int icon;
    private String name;
    private String details;

    public Service (String name,int image, int icon){
        this.image=image;
        this.name=name;
        this.icon = icon;
    }

    public Service(String name, int image, int icon, String details) {
        this.name = name;
        this.image = image;
        this.icon = icon;
        this.details = details;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
