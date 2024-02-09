package com.example.cln.Models;

import com.google.android.gms.maps.model.LatLng;

public class Model implements ModelInterface {
    private Long id;
    private String label;
    private LatLng latLng;


    public Model(String label, LatLng latLng) {
        this.label = label;
        this.latLng = latLng;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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
