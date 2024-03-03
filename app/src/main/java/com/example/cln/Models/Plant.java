package com.example.cln.Models;

import android.content.ContentValues;

import com.example.cln.R;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Plant model
 */
public class Plant extends MultiPointModel implements IArea {
    private Integer quantity;
    private final Integer growthState;
    private final Integer leafAmount;

    public Plant(String label, ArrayList<LatLng> latLngs, int quantity, Integer growthState,
                 Integer leafAmount) {
        super(label, latLngs, null, "plant");

        final int resourceId;
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

        super.setResourceId(resourceId);

        this.quantity = quantity;
        this.growthState = growthState;
        this.leafAmount = leafAmount;
    }


    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getGrowthState() {
        return growthState;
    }
    public Integer getLeafAmount() {
        return leafAmount;
    }

    @Deprecated
    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();
        contentValues.put("label", getLabel());
//        contentValues.put("points", getLatLngs());
        contentValues.put("growth_state_id", getGrowthState());
        contentValues.put("leaf_amount", getLeafAmount());

        return contentValues;
    }

    @Override
    public JSONObject toJSONObject() {
        JSONObject jsonObject = super.toJSONObject();

        try {
            jsonObject.put("quantity", quantity);
            jsonObject.put("growth_state_id", growthState);
            jsonObject.put("leaf_amount", leafAmount);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return jsonObject;
    }

    /**
     * Returns the Model based on the JSON object
     * @return Returns the instance of the Model
     */
    public static Plant fromJSONObject(JSONObject jsonObject) {
        try {
            JSONArray JSONpoints = new JSONArray(jsonObject.getString("points"));
            ArrayList<LatLng> points = new ArrayList<>();

            for (int i=0; i < JSONpoints.length(); i++) {
                JSONArray coords = JSONpoints.getJSONArray(i);
                points.add(new LatLng(coords.getDouble(0), coords.getDouble(1)));
            }

            Plant plant = new Plant(
                    jsonObject.getString("label"),
                    points,
                    Integer.parseInt(jsonObject.getString("leaf_amount")),
                    Integer.parseInt(jsonObject.getString("growth_state_id")),
                    Integer.parseInt(jsonObject.getString("leaf_amount"))
            );

            plant.setId(Long.parseLong(jsonObject.getString("plant_id")));
            return plant;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
