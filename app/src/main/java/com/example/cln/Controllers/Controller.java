package com.example.cln.Controllers;

import android.content.Context;

import com.example.cln.Models.Model;
import com.example.cln.Storers.LocalAccess;
import com.example.cln.Storers.RemoteAccess;
import com.google.android.gms.maps.model.Marker;

public class Controller {
    private static Controller instance;
    private final MapController mapController;
    private final LocalAccess localAccess;
    private final RemoteAccess remoteAccess;

    private Controller(Context context) {
        mapController = MapController.getInstance(context);
        localAccess = LocalAccess.getInstance(context);
        remoteAccess = RemoteAccess.getInstance(context);
    }

    public static Controller getInstance(Context context) {
        if (instance == null) {
            instance = new Controller(context);
        }

        return instance;
    }

    public void addEntry(Model model) {
        Marker marker = mapController.addMapMarker(model);
        localAccess.addEntry(model, marker);
        remoteAccess.add(model.toJSONObject());
    }

    public void retrieveEntries() {
        populateMap(localAccess.retrieveEntries());
    }

    public void populateMap(Model[] models) {
        for (Model model : models) {
            Marker marker = mapController.addMapMarker(model);
            marker.setTag(model.getId());
            localAccess.addModel((Long) marker.getTag(), model);
        }
    }

    public void updateModel(Model model) {
        localAccess.updateEntry(model);
    }

    public void updateModel(Marker marker) {
        Model model = getModel((Long) marker.getTag());

        model.setLatLng(marker.getPosition());
        localAccess.updateEntry(model);
    }

    private Model getModel(Long id) {
        return localAccess.getModel(id);
    }

    public void updateEntry(Marker marker, String label, boolean draggable) {
        mapController.updateMarker(marker, label, draggable);
        Model model = getModel((Long) marker.getTag());
        model.setLabel(label);
        localAccess.updateEntry(model);
    }

    public void deleteEntry(Marker selectedMarker) {
        localAccess.deleteEntry(selectedMarker);
        selectedMarker.remove();
    }
}
