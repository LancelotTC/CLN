package com.example.cln.Models;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Model child class which represents objects which have more than 1 set of coordinates.
 * This includes polygons and poly-lines.
 */
public class MultiPointModel extends Model {

    private ArrayList<LatLng> latLngs;

    public MultiPointModel(String label, ArrayList<LatLng> latLngs, Integer resourceId, String tableName) {
        super(label, resourceId, tableName);
        this.latLngs = latLngs;
    }

    public ArrayList<LatLng> getLatLngs() {
        return latLngs;
    }

    public void setLatLngs(ArrayList<LatLng> latLngs) {
        this.latLngs = latLngs;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject jsonObject = super.toJSONObject();

        try {
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
