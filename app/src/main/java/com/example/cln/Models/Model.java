package com.example.cln.Models;

import com.google.android.gms.maps.model.LatLng;

public class Model implements ModelInterface {
    private long id;
    private final String label;
    private LatLng latLng;


    public Model(String label, LatLng latLng) {
        this.label = label;
        this.latLng = latLng;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public LatLng getLatLng() {
        return latLng;
    }

    @Override
    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }
}
