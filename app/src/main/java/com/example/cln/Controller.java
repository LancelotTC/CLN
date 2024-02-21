package com.example.cln;

import android.content.Context;

import com.example.cln.Models.Model;
import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;

public class Controller {
    private static Controller instance;
    private final MapController mapController;
    private final LocalAccess localAccess;
    private final RemoteAccess remoteAccess;
    private final HashMap<Long, Model> objectToMarker;

    private Controller(Context context) {
        mapController = MapController.getInstance(context);
        localAccess = LocalAccess.getInstance(context);
        remoteAccess = RemoteAccess.getInstance(context);
        objectToMarker = new HashMap<>();
    }

    public static Controller getInstance(Context context) {
        if (instance == null) {
            instance = new Controller(context);
        }

        return instance;
    }

    public void addEntry(Model model) {
        Marker marker = mapController.addMarker(model);
//        localAccess.addEntry(model, marker);
        objectToMarker.put((Long) marker.getTag(), model);
        remoteAccess.add(model);
    }

    public void retrieveEntries() {
        remoteAccess.getAll();
//        populateMap(localAccess.retrieveEntries());
    }

    public void populateMap(Model[] models) {
        for (Model model : models) {
            Marker marker = mapController.addMarker(model);
            marker.setTag(model.getId());
            objectToMarker.put((Long) marker.getTag(), model);

            // localAccess.addModel((Long) marker.getTag(), model);
        }
    }

    public void updateModel(Marker marker) {
        Model model = getModel((Long) marker.getTag());
        model.setLatLng(marker.getPosition());
//        localAccess.updateEntry(model);
        remoteAccess.update(model);
    }

    private Model getModel(Long markerTag) {
//        return localAccess.getModel(id);
        return objectToMarker.get(markerTag);
    }

    public void updateEntry(Marker marker, String label, boolean draggable) {
        mapController.updateMarker(marker, label, draggable);
        Model model = getModel((Long) marker.getTag());
        model.setLabel(label);
//        localAccess.updateEntry(model);
        remoteAccess.update(model);
    }

    public void deleteEntry(Marker selectedMarker) {
//        localAccess.deleteEntry(selectedMarker);
        remoteAccess.delete(getModel((Long) selectedMarker.getTag()));
        objectToMarker.remove((Long) selectedMarker.getTag());
        selectedMarker.remove();
    }
}
