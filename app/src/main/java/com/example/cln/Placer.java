package com.example.cln;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;

public class Placer {
    private MapController mapController;
    private final ArrayList<Marker> points = new ArrayList<>();

    private Polygon polygon;

    public Placer(Context context) {
        mapController = MapController.getInstance(context);
    }

    public void addPoint(Marker marker) {
        points.add(marker);
    }

    public void createPolygon() {
        ArrayList<LatLng> latLngs = new ArrayList<>();

        for (Marker marker : points) {latLngs.add(marker.getPosition());}

        polygon = mapController.addPolygon(new PolygonOptions().addAll(latLngs));
    }

    public Polygon getPolygon() {
        return polygon;
    }

    public void removeMarkers() {
        for (Marker point : points) {point.remove();}
    }
}
