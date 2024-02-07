package com.example.cln.Models;

import android.content.ContentValues;

import com.example.cln.R;
import com.google.android.gms.maps.model.LatLng;

public class Tree extends Model {

    public Tree(String label, LatLng latLng) {
        super(label, latLng);
    }

    public Integer getResourceId() {
        return R.drawable.tree_icon;
    }

    public String getTableName() {
        return "tree";
    }
    public ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();

        contentValues.put("label", getLabel());
        contentValues.put("latitude", getLatLng().latitude);
        contentValues.put("longitude", getLatLng().longitude);

        return contentValues;
    }
}
