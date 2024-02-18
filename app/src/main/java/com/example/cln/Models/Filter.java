package com.example.cln.Models;

import android.content.ContentValues;

import com.example.cln.R;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

public class Filter extends Model {
    public Filter(String label, LatLng latLng) {
        super(label, latLng);
    }
    public Integer getResourceId() {
        return R.drawable.filter_icon;
    }

    @Override
    public String getTableName() {
        return null;
    }
    @Override
    public ContentValues getContentValues() {
        return null;
    }

    public static Filter fromJSONObject(JSONObject jsonObject) {
        try {
            Filter filter = new Filter(
                    jsonObject.getString("label"),
                    new LatLng(jsonObject.getDouble("latitude"),
                            jsonObject.getDouble("longitude"))
            );

            filter.setId(jsonObject.getLong("filter_id"));

            return filter;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

}
