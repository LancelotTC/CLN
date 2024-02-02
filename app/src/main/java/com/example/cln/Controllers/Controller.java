package com.example.cln.Controllers;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Controller {
    private static Controller instance;


    private Controller() {super();}

    public static Controller getInstance() {
        if (instance == null) {
            instance = new Controller();
        }

        return instance;
    }

    public void addMapMarker(GoogleMap googleMap, float latitude, float longitude, String title,
                             int ressourceId) {
        addMapMarker(googleMap, new LatLng(latitude, longitude), title, ressourceId);
    }

    public void addMapMarker(GoogleMap googleMap, LatLng latLng, String title, int ressourceId) {
        MarkerOptions marker = new MarkerOptions();
        marker.position(latLng).title(title);
        googleMap.addMarker(marker);
    }



}
