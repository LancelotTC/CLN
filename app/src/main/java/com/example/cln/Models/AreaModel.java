package com.example.cln.Models;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AreaModel extends Model {

    private int quantity;

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
            jsonObject.put("quantity", quantity);

            ArrayList<Double[]> positions = new ArrayList<>();
            for (LatLng latLng : latLngs) {
                positions.add(new Double[] {latLng.latitude, latLng.longitude});
            }

            // db only accepts values as strings, not json arrays
            jsonObject.put("points", new JSONArray(positions).toString());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return jsonObject;
    }
}
