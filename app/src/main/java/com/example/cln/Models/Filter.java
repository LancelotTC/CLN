package com.example.cln.Models;

import com.example.cln.R;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

public class Filter extends PointModel {
    public Filter(String label, LatLng latLng) {
        super(label, latLng, R.drawable.filter_icon, "filter");
    }

    public static Filter fromJSONObject(JSONObject jsonObject) {
        try {
            Filter filter = new Filter(
                    // TODO: change the way location is accessed.
                    jsonObject.getString("label"),
                    new LatLng(
                            Double.parseDouble(jsonObject.getString("latitude")),
                            Double.parseDouble(jsonObject.getString("longitude"))
                    )
            );

            filter.setId(Long.parseLong(jsonObject.getString("filter_id")));

            return filter;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

}
