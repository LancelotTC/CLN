package com.example.cln.Models;

import android.content.ContentValues;

import com.google.android.gms.maps.model.LatLng;

public interface Model {

    public default String getSQLInsertQuery() {
        return null;
    }

    public default Integer getRessourceId() {
        return null;
    }

    public default LatLng getLatLng() {
        return null;
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
