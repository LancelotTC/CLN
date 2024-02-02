package com.example.cln.Controllers;

//import static androidx.appcompat.graphics.drawable.DrawableContainerCompat.Api21Impl.getResources;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.cln.Models.Model;
import com.example.cln.Models.Plant;
import com.example.cln.R;
import com.example.cln.Storers.LocalAccess;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Objects;

public class MapController {
    private static MapController instance;
    private GoogleMap googleMap;
    private boolean locationPermissionGranted;
    private Location lastKnownLocation;
    private LocalAccess localAccess;

    // Sole context is MainActivity since the map is only present in said activity, so it's fine
    // to store it in the controller
    private final Context context;

    private MapController(Context context) {
        this.context = context;
        localAccess = LocalAccess.getInstance(context);
        getLocationPermission();
    }

    public static MapController getInstance(Context context) {
        if (instance == null) {
            instance = new MapController(context);
        }
        return instance;
    }

    public void setMap(GoogleMap googleMap) {
        this.googleMap = googleMap;
        requestLastKnownLocation();

    }

    public void addMapMarkerToCurrentLocation(String title, int resourceId) {
        addMapMarker(getCurrentLocation(), title, resourceId);
    }
    public void addMapMarkerToCurrentScreenLocation(String title, int resourceId) {
        addMapMarker(getCurrentScreenLocation(), title, resourceId);
    }

    public LatLng getCurrentScreenLocation() {
        int mWidth= context.getResources().getDisplayMetrics().widthPixels;
        int mHeight= context.getResources().getDisplayMetrics().heightPixels;

        return googleMap.getProjection().fromScreenLocation(new Point(mWidth, mHeight));
    }

    @NonNull
    public LatLng getCurrentLocation() {
        return new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
    }

    public void addMapMarker(float latitude, float longitude, String title, int resourceId) {
        addMapMarker(new LatLng(latitude, longitude), title, resourceId);
    }
    public void addMapMarker(double latitude, double longitude, String title, int resourceId) {
        addMapMarker(new LatLng(latitude, longitude), title, resourceId);
    }

    @NonNull
    private BitmapDescriptor bitmapDescriptorFromVector(int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        assert vectorDrawable != null;
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

//        Canvas canvas = new Canvas(bitmap);
        Matrix matrix = new Matrix();
        matrix.postScale(.9F, .9F);
        Canvas canvas = new Canvas(Bitmap.createBitmap(bitmap, 0, 0, 336, 336,
                matrix, false));


        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public Bitmap resizeMapIcon() {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, R.drawable.plant_icon);
        assert vectorDrawable != null;
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Bitmap imageBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.plant_icon);
        return Bitmap.createScaledBitmap(imageBitmap, 50, 50, false);
    }

    public void addEntry(Model model) {
        localAccess.addEntry(model);
        addMapMarker(model.getLatLng(), model.getLabel(), model.getRessourceId());
    }

    public void addMapMarker(LatLng latLng, String title, int resourceId) {
        MarkerOptions markerOptions = new MarkerOptions();

        markerOptions.position(latLng);

        markerOptions.title(title);

        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

        Marker marker = Objects.requireNonNull(googleMap.addMarker(markerOptions));
        marker.setDraggable(true);
        //        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.plant_icon));

        marker.setIcon(bitmapDescriptorFromVector(resourceId));
    }
    public void moveToCurrentLocation() {
        if (lastKnownLocation == null) {
            return;
        }

        moveCamera(getCurrentLocation(), 30);
    }

    public void moveCamera(LatLng latLng, int zoom) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }
    public void moveCamera(LatLng latLng) {
        moveCamera(latLng, 30);
    }

    public void requestLastKnownLocation() {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

        try {
            if (locationPermissionGranted) {

                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener((Activity) context, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
//                            moveToCurrentLocation();

                        } else {
                            Log.d("INFO", "Current location is null. Using defaults.");
                            Log.e("INFO", "Exception: %s", task.getException());
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    public void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(context.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
    }

    public void simulateClickOnMap() {

    }

    public void addCustomLocation(Context context) {
        Geocoder geocoder = new Geocoder(context);
    }

    public Polygon addPolygon(PolygonOptions rectOptions) {
        return googleMap.addPolygon(rectOptions);
    }

    public void drawBoundaries() {
        LocalAccess localAccess = LocalAccess.getInstance(context);

        localAccess.getNextRegionPoints();

    }
}
