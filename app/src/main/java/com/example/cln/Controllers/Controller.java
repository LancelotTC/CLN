package com.example.cln.Controllers;

import android.content.Context;
import android.util.Log;

import com.example.cln.Models.Model;
import com.example.cln.Storers.LocalAccess;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Controller {
    private static Controller instance;
    private LocalAccess localAccess;
    private MapController mapController;


    private Controller(Context context) {
        localAccess = LocalAccess.getInstance(context);
        mapController = MapController.getInstance(context);
    }

    public static Controller getInstance(Context context) {
        if (instance == null) {
            instance = new Controller(context);
        }

        return instance;
    }

    public void addEntry(Model model) {
        localAccess.addEntry(model);
        mapController.addMapMarker(model.getLatLng(), model.getLabel(), model.getResourceId());
    }

    public void retrieveEntries() {
        Model[] models = localAccess.retrieveEntries();
        for (Model model : models) {
            Log.d("Map controller", "Added a model to the map");
            mapController.addMapMarker(model);
        }
    }
}
