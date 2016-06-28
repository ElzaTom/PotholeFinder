package com.example.elzatom.potholefinder.model;

import java.util.Date;

/**
 * Created by elzatom on 3/16/16.
 */
public class Pothole {

    private int id;
    private String description;
    private Date createdDate;
    private double latitude;
    private double longitude;
    private String imageType;
    private String type;


    public Pothole() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Double getLatitude() {
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

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public void setType(String type) {
        this.type = type;
    }


}
