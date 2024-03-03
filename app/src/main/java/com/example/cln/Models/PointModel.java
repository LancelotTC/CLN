package com.example.cln.Models;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Model child class which represents objects which have exactly 1 set of coordinates.
 * This only includes Markers.
 */
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

    public JSONObject toJSONObject() {
        JSONObject jsonObject = super.toJSONObject();

        try {
            jsonObject.put("latitude", latLng.latitude);
            jsonObject.put("longitude", latLng.longitude);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return jsonObject;
    }


}
