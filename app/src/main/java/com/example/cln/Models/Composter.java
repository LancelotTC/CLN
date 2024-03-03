package com.example.cln.Models;

import com.example.cln.R;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Composter model.
 */
public class Composter extends PointModel {
    public Composter(String label, LatLng latLng) {
        super(label, latLng, R.drawable.terrain_icon, "composter");
    }

    /**
     * Returns the Model based on the JSON object
     * @return Returns the instance of the Model
     */
    public static Composter fromJSONObject(JSONObject jsonObject) {
        try {
            Composter composter = new Composter(
                    // TODO: change the way location is accessed.
                    jsonObject.getString("label"),
                    new LatLng(Double.parseDouble(jsonObject.getString("latitude")),
                            Double.parseDouble(jsonObject.getString("longitude")))
            );

            composter.setId(Long.parseLong(jsonObject.getString("composter_id")));
            return composter;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
