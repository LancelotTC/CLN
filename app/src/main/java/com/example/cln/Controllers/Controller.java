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

    public void addMapMarker(GoogleMap googleMap, float latitude, float longitude, String title) {
        addMapMarker(googleMap, new LatLng(latitude, longitude), title);
    }

    public void addMapMarker(GoogleMap googleMap, LatLng latLng, String title) {
        googleMap.addMarker(new MarkerOptions().position(latLng).title("Maison"));
    }



}
