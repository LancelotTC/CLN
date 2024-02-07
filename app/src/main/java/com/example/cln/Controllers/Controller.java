package com.example.cln.Controllers;

import android.content.Context;
import android.util.Log;

import com.example.cln.Models.ModelInterface;
import com.example.cln.Storers.LocalAccess;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;

public class Controller {
    private static Controller instance;
    private LocalAccess localAccess;
    private MapController mapController;
    private HashMap<String, ModelInterface> objectToMarker;


    private Controller(Context context) {
        localAccess = LocalAccess.getInstance(context);
        mapController = MapController.getInstance(context);
        objectToMarker = new HashMap<>();
    }

    public static Controller getInstance(Context context) {
        if (instance == null) {
            instance = new Controller(context);
        }

        return instance;
    }

    public void addEntry(ModelInterface model) {
        localAccess.addEntry(model);
        Marker marker = mapController.addMapMarker(model.getLatLng(), model.getLabel(), model.getResourceId());
        objectToMarker.put(marker.getId(), model);
    }

    public ModelInterface getModel(String markerId) {
        return objectToMarker.get(markerId);
    }

    public void retrieveEntries() {
        ModelInterface[] models = localAccess.retrieveEntries();
        for (ModelInterface model : models) {
            Log.d("Map controller", model.toString());
            mapController.addMapMarker(model);
        }
    }

    public void updateModel(ModelInterface model) {
        localAccess.updateEntry(model);
    }
}
