package com.cypien.leroy.models;

/**
 * Created by Alex on 9/16/2015.
 */

public class Category {

    private String name;
    private boolean selected;

    public Category(String name) {
        this.name = name;
        selected = false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}