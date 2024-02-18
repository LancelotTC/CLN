package com.example.cln.Models;

import android.content.ContentValues;

import com.example.cln.R;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

public class Composter extends Model {
    public Composter(String label, LatLng latLng) {
        super(label, latLng);
    }

    public Integer getResourceId() {
        return R.drawable.terrain_icon;
    }

    @Override
    public String getTableName() {
        return null;
    }

    @Override
    public ContentValues getContentValues() {
        return null;
    }

    public static Composter fromJSONObject(JSONObject jsonObject) {
        try {
            Composter composter = new Composter(
                    jsonObject.getString("label"),
                    new LatLng(jsonObject.getDouble("latitude"),
                            jsonObject.getDouble("longitude"))
            );

            composter.setId(jsonObject.getLong("composter_id"));
            return composter;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

}
