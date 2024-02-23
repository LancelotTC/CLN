package com.example.cln.Models;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public interface IAreaModel {
    int amount = 0;

    int getAmount();
    ArrayList<LatLng> getLatlngs();
}
