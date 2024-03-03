package com.example.cln.Models;

import com.example.cln.R;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Tree Model
 */
public class Tree extends PointModel {

    public Tree(String label, LatLng latLng) {
        super(label, latLng, R.drawable.tree_icon, "tree");
    }

    /**
     * Returns the Model based on the JSON object
     * @return Returns the instance of the Model
     */
    public static Tree fromJSONObject(JSONObject jsonObject) {
        try {
            Tree tree = new Tree(
                    // TODO: change the way location is accessed.
                    jsonObject.getString("label"),
                    new LatLng(
                            Double.parseDouble(jsonObject.getString("latitude")),
                            Double.parseDouble(jsonObject.getString("longitude"))
                    )
            );

            tree.setId(Long.parseLong(jsonObject.getString("tree_id")));
            return tree;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
