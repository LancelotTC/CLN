package com.example.cln.Models;

import android.content.ContentValues;

import com.example.cln.R;
import com.google.android.gms.maps.model.LatLng;

public class Plant implements Model {
    private final String label;
    private final Double latitude;
    private final Double longitude;
    private final Integer growthState;
    private final Integer leafAmount;
    private final Integer resourceId = R.drawable.plant_icon;

    public Plant(String label, Double latitude, Double longitude, Integer growthState, Integer leafAmount) {
        this.label = label;
        this.latitude = latitude;
        this.longitude = longitude;
        this.growthState = growthState;
        this.leafAmount = leafAmount;
    }
    public Plant(String label, LatLng latLng, Integer growthState, Integer leafAmount) {
            this(label,latLng.latitude, latLng.latitude, growthState, leafAmount);
        }
    @Deprecated
    public String getSQLInsertQuery() {
        return "insert into plant (label, latitude, longitude, growth_state_id, leaf_amount) values ('" +
                getLabel() + "', " + getLatitude() + ", " + getLongitude()  +
                ", " + getGrowthState() + ", " + getLeafAmount() +")";
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

    public Integer getGrowthState() {
        return growthState;
    }

    public Integer getLeafAmount() {
        return leafAmount;
    }
    public Integer getResourceId() {
        return resourceId;
    }
    public String getTableName() {
        return "plant";
    }
    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("label", getLabel());
        contentValues.put("latitude", getLatitude());
        contentValues.put("longitude", getLongitude());
        contentValues.put("growth_state_id", getGrowthState());
        contentValues.put("nb_leaves", getLeafAmount());

        return contentValues;
    }

}
