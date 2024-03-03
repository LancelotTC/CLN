package com.example.cln.View;

import android.content.Context;

import com.example.cln.Controllers.MapController;
import com.example.cln.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

/**
 * Handles the conversion from markers to a polygon.
 */
public class Placer {
    /**
     * MapController instance
     */
    private final MapController mapController;

    /**
     * ArrayList of Markers that will shape the polygon/poly-line
     */
    private final ArrayList<Marker> points = new ArrayList<>();

    /**
     * Resulting polygon
     */
    private Polygon polygon;

    /**
     * Resulting poly-line
     */
    private Polyline polyline;

    /**
     * @param context Context
     */
    public Placer(Context context) {
        mapController = MapController.getInstance(context);
//        mapController.setOnMapClickListener(null);
        mapController.setObjectsClickable(false);
        mapController.setOnMapClickListener(latLng ->
                points.add(mapController.addMarker(latLng, "Point", R.drawable.point_icon)));
    }

    /**
     * Places the polygon and stores it for later use.
     */
    public void createPolygon() {
        polygon = mapController.addPolygon(new PolygonOptions().addAll(getLatLngs()));
    }

    /**
     * Places the poly-line and stores it for later use.
     */
    public void createPolyline() {
        polyline = mapController.addPolyline(new PolylineOptions().addAll(getLatLngs()));
    }

    /**
     * Returns the latLngs from the points
     * @return latLngs from the points
     */
    public ArrayList<LatLng> getLatLngs() {
        ArrayList<LatLng> latLngs = new ArrayList<>();

        for (Marker point : points) {
            latLngs.add(point.getPosition());
        }
        return latLngs;
    }

    /**
     * Removes all markers, used when creating a shape or cancelling operation.
     */
    public void removeMarkers() {
        for (Marker point : points) {point.remove();}
    }

    /**
     * Removes the shape
     */
    public void removeShape() {
        if (polygon != null) {
            polygon.remove();
        }

        if (polyline != null) {
            polyline.remove();
        }
    }

    /**
     * Cancel operation.
     */
    public void cancel() {
        removeShape();

        mapController.setObjectsClickable(true);
        removeMarkers();
    }

    /**
     * Method that places a marker in the center location of a shape.
     */
    public void placeFlagMarker() {
        double averageX = 0D;
        double averageY = 0D;

        for (Marker point : points) {
            averageX += point.getPosition().latitude;
            averageY += point.getPosition().longitude;
        }

        averageX /= points.size();
        averageY /= points.size();

        mapController.addMarker(new LatLng(averageX, averageY), "", R.drawable.plant_ps_icon);
    }
}
