package com.example.cln.Controllers;

import android.content.Context;
import android.util.Log;

import com.example.cln.Models.Model;
import com.example.cln.Lambda;
import com.google.android.gms.maps.model.Marker;

import java.util.function.Consumer;

public class Controller {
    private static Controller instance;
    private final MapController mapController;
    private final DatabaseController databaseController;

    private Controller(Context context) {
        mapController = MapController.getInstance(context);
        databaseController = DatabaseController.getInstance(context);
    }

    public static Controller getInstance(Context context) {
        if (instance == null) {
            instance = new Controller(context);
        }

        return instance;
    }

    public void addEntry(Model model) {
        Marker marker = mapController.addMapMarker(model.getLatLng(), model.getLabel(), model.getResourceId());
        databaseController.addEntry(model, marker);
    }

    public void retrieveEntries() {
        for (Model model : databaseController.retrieveEntries()) {

            Marker marker = mapController.addMapMarker(model);
            marker.setTag(model.getId());
            databaseController.addModel((Long) marker.getTag(), model);
        }
    }

    public void updateModel(Model model) {
        databaseController.updateEntry(model);
    }

    public void updateModel(Marker marker) {
        Model model = getModel((Long) marker.getTag());

        model.setLatLng(marker.getPosition());
        updateModel(model);
    }

    public Model getModel(Long id) {
        return databaseController.getModel(id);
    }

    public void showMarkerInfo(Marker marker) {
        Model model = getModel((Long) marker.getTag());
        // show information screen
    }

    public void setOnMarkerClickListener(Consumer<Marker> consumer) {
        mapController.setOnMarkerClickListener(consumer);
    }

    public void setOnMapClickListener(Lambda func) {
        mapController.setOnMapClickListener(func);

    }

    public void updateEntry(Marker marker, String label, boolean draggable) {
        mapController.updateMarker(marker, label, draggable);
        Model model = getModel((Long) marker.getTag());
        model.setLabel(label);
        databaseController.updateEntry(model);
    }
}
