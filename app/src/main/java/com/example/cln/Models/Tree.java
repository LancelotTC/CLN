package com.example.cln.Models;

import android.content.ContentValues;

import com.example.cln.R;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

public class Tree extends Model {

    public Tree(String label, LatLng latLng) {
        super(label, latLng);
    }

    public Integer getResourceId() {
        return R.drawable.tree_icon;
    }

    public String getTableName() {
        return "fruit_tree";
    }
    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();

        contentValues.put("label", getLabel());
        contentValues.put("latitude", getLatLng().latitude);
        contentValues.put("longitude", getLatLng().longitude);

        return contentValues;
    }

    public static Tree fromJSONObject(JSONObject jsonObject) {
        try {
            Tree tree = new Tree(
                    jsonObject.getString("label"),
                    new LatLng(jsonObject.getDouble("latitude"),
                            jsonObject.getDouble("longitude"))
            );

            tree.setId(jsonObject.getLong("fruit_tree_id"));
            return tree;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
