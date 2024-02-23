package com.example.cln.Models;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AreaModel extends Model {

    private int amount;

    private ArrayList<LatLng> latLngs;

    public AreaModel(String label, ArrayList<LatLng> latLngs, Integer resourceId, String tableName) {
        super(label, resourceId, tableName);
        this.latLngs = latLngs;
    }

    public ArrayList<LatLng> getLatLngs() {
        return latLngs;
    }

    public void setLatLngs(ArrayList<LatLng> latLngs) {
        this.latLngs = latLngs;
    }

    public JSONObject toJSONObject() {
        JSONObject jsonObject = super.toJSONObject();

        try {
            jsonObject.put("amount", amount);
            jsonObject.put("points", latLngs);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return jsonObject;
    }
}
