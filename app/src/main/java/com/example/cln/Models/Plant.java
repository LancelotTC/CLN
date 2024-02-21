package com.example.cln.Models;

import android.content.ContentValues;

import com.example.cln.R;
import com.example.cln.Utils.Shortcuts;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

public class Plant extends Model implements ModelInterface {
    private final Integer growthState;
    private final Integer leafAmount;
    private final int resourceId;

    public Plant(String label, LatLng latLng, Integer growthState, Integer leafAmount) {
        super(label, latLng);

        this.growthState = growthState;
        this.leafAmount = leafAmount;

        switch (growthState) {
            case 1:
                resourceId = R.drawable.plant_ps_icon;
                break;
            case 2:
                resourceId = R.drawable.plant_ppl_icon;
                break;
            case 3:
                resourceId = R.drawable.plant_icon;
                break;
            default:
                throw new RuntimeException("(User thrown) Invalid growthState: " + growthState);
        }
    }

    public Integer getGrowthState() {
        return growthState;
    }

    public Integer getLeafAmount() {
        return leafAmount;
    }
    public Integer getResourceId() {
        return resourceId;
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

        Shortcuts.log("JSONObject", jsonObject);
        return jsonObject;
    }

    public static Plant fromJSONObject(JSONObject jsonObject) {
        try {
            Plant plant = new Plant(
                    jsonObject.getString("label"),
                    new LatLng(jsonObject.getDouble("latitude"),
                            jsonObject.getDouble("longitude")),
                    jsonObject.getInt("growth_state_id"),
                    jsonObject.getInt("leaf_amount")
            );

            plant.setId(jsonObject.getLong("plant_id"));
            return plant;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
