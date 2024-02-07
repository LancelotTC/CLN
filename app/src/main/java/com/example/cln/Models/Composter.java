package com.example.cln.Models;

import com.example.cln.R;
import com.google.android.gms.maps.model.LatLng;

public class Composter extends Model {
    public Composter(String label, LatLng latLng) {
        super(label, latLng);
    }

    public Integer getResourceId() {
        return R.drawable.terrain_icon;
    }



}
