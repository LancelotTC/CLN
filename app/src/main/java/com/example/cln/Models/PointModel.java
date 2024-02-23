package com.example.cln.Models;

import com.google.android.gms.maps.model.LatLng;

public class PointModel extends Model {
    private LatLng latLng;

    public PointModel(String label, LatLng latLng, Integer resourceId, String tableName) {
        super(label, resourceId, tableName);
        this.latLng = latLng;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }
}
