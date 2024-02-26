package com.example.cln;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;

/**
 * Handles the conversion from markers to a polygon.
 */
public class Placer {
    private final MapController mapController;
    private final ArrayList<Marker> points = new ArrayList<>();
    private final ArrayList<LatLng> latLngs = new ArrayList<>();

    private Polygon polygon;

    public Placer(Context context) {
        mapController = MapController.getInstance(context);
//        mapController.setOnMapClickListener(null);
        mapController.setObjectsClickable(false);
        mapController.setOnMapClickListener(latLng -> {
            addPoint(mapController.addMarker(
                    latLng, "Marker for plant",
                    R.drawable.point_icon)
            );
        });
    }

    public void addPoint(Marker marker) {
        points.add(marker);
        latLngs.add(marker.getPosition());
    }

    public void createPolygon() {
        polygon = mapController.addPolygon(new PolygonOptions().addAll(latLngs));
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public ArrayList<Marker> getPoints() {
        return points;
    }

    public ArrayList<LatLng> getLatLngs() {
        ArrayList<LatLng> latLngs = new ArrayList<>();

        for (Marker point : points) {
            latLngs.add(point.getPosition());
        }
        return latLngs;
    }

    public void removeMarkers() {
        for (Marker point : points) {point.remove();}
    }

    public void cancel() {
        if (polygon != null) {
            polygon.remove();
        }

        mapController.setObjectsClickable(true);
        removeMarkers();
    }

    public void removePolygon() {
        if (polygon == null) {
            return;
        }
        polygon.remove();
    }
}
