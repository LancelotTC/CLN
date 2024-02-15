package com.example.cln.Models;

import android.content.ContentValues;

import com.example.cln.R;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

public class Plant extends Model {
    private final Integer growthState;
    private final Integer leafAmount;

    public Plant(String label, LatLng latLng, Integer growthState, Integer leafAmount) {
        super(label, latLng);

        this.growthState = growthState;
        this.leafAmount = leafAmount;
    }

    public Integer getGrowthState() {
        return growthState;
    }

    public Integer getLeafAmount() {
        return leafAmount;
    }
    public Integer getResourceId() {
        return R.drawable.plant_icon;
    }
    public String getTableName() {
        return "plant";
    }
    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("label", getLabel());
        contentValues.put("latitude", getLatLng().latitude);
        contentValues.put("longitude", getLatLng().longitude);
        contentValues.put("growth_state_id", getGrowthState());
        contentValues.put("leaf_amount", getLeafAmount());

        return contentValues;
    }

    public JSONObject toJSONObject() {
        JSONObject jsonObject = super.toJSONObject();

        try {
            jsonObject.put("growth_state_id", getGrowthState());
            jsonObject.put("leaf_amount", getLeafAmount());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return jsonObject;
    }

}
