package com.example.cln.Models;

import com.example.cln.R;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Filter model
 */
public class Filter extends MultiPointModel implements ILine {
    public Filter(String label, ArrayList<LatLng> latLngs) {
        super(label, latLngs, R.drawable.filter_icon, "filter");
    }

    /**
     * Returns the Model based on the JSON object
     * @return Returns the instance of the Model
     */
    public static Filter fromJSONObject(JSONObject jsonObject) {
        try {
            JSONArray JSONpoints = new JSONArray(jsonObject.getString("points"));
            ArrayList<LatLng> points = new ArrayList<>();

            for (int i=0; i < JSONpoints.length(); i++) {
                JSONArray coords = JSONpoints.getJSONArray(i);
                points.add(new LatLng(coords.getDouble(0), coords.getDouble(1)));
            }

            Filter filter = new Filter(
                    jsonObject.getString("label"),
                    points
            );

            filter.setId(Long.parseLong(jsonObject.getString("filter_id")));
            return filter;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
