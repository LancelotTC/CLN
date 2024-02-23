package com.example.cln.Models;

import com.example.cln.R;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

public class Tree extends PointModel {

    public Tree(String label, LatLng latLng) {
        super(label, latLng, R.drawable.tree_icon, "tree");
    }

    public static Tree fromJSONObject(JSONObject jsonObject) {
        try {
            Tree tree = new Tree(
                    // TODO: change the way location is accessed.
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
