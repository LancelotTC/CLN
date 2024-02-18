package com.example.cln.Models;

import android.content.ContentValues;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class Model {
    private Long id;
    private String label;
    private LatLng latLng;


    public Model(String label, LatLng latLng) {
        this.label = label;
        this.latLng = latLng;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public abstract Integer getResourceId();
    public abstract String getTableName();
    public abstract ContentValues getContentValues();
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public JSONObject toJSONObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("label", label);
            jsonObject.put("latitude", getLatLng().latitude);
            jsonObject.put("longitude", getLatLng().longitude);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return jsonObject;
    }

}
