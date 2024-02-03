package com.example.cln.Models;

import android.content.ContentValues;

import com.example.cln.R;
import com.google.android.gms.maps.model.LatLng;

public class Tree implements Model {
    private final String label;
    private final Double latitude;
    private final Double longitude;
    private final Integer id = R.drawable.tree_icon;


    public Tree(String label, Double latitude, Double longitude) {
        this.label = label;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Tree(String label, LatLng latLng) {
        this(label, latLng.latitude, latLng.longitude);
    }

    @Deprecated
    public String getSQLInsertQuery() {
        return "(label, latitude, longitude) values (" + getLabel() + getLatitude() + ", " + getLongitude() + ");";
    }
    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }
    public String getLabel() {
        return label;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Integer getResourceId() {
        return id;
    }

    public String getTableName() {
        return "tree";
    }
    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("label", getLabel());
        contentValues.put("latitude", getLatitude());
        contentValues.put("longitude", getLongitude());

        return contentValues;
    }
}
