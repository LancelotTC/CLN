package com.example.cln.Models;

import android.content.ContentValues;

import com.google.android.gms.maps.model.LatLng;

public interface ModelInterface {

    public default long getId() {
        return -1;
    }
    public default void setId(long id) {
        return;
    };

    public default Integer getResourceId() {
        return null;
    }
    public default LatLng getLatLng() {
        return null;
    }

    public default void setLatLng(Double latitude, Double longitude) {
        return;
    }
    public default void setLatLng(LatLng latLng) {
        return;
    }
    public default String getLabel() {
        return null;
    }
    public default String getTableName() {
        return null;
    }
    public default ContentValues getContentValues() {
        return null;
    }
}
