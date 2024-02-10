package com.example.cln.Controllers;

//import static androidx.appcompat.graphics.drawable.DrawableContainerCompat.Api21Impl.getResources;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.cln.Lambda;
import com.example.cln.Models.Model;
import com.example.cln.R;
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
import com.google.android.gms.tasks.Task;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Controller specifically for the map object. Instantiates and manages everything on the map.
 */
public class MapController {
    /**
     * Static instance of class for Singleton design pattern
     */
    private static MapController instance;

    /**
     * Instance of the map
     */
    private GoogleMap googleMap;

    /**
     * Whether the location permission has been granted by the user
     */
    private boolean locationPermissionGranted;

    /**
     * Stores the last known location (usually the current location)
     */
    private Location lastKnownLocation;
//    private boolean currentLocationRequested = false;
//    private boolean currentLocationAvailable = false;


    /**
     * Since the map is in MainActivity and this is the MapController, the only context will be
     * MainActivity so it's fine to store it once and for all in the controller instead of passing
     * it to every method call
     */
    private final Context context;

    /**
     * Stores the selected marker on the map or null if it doesn't exist
     */
    private Marker selectedMarker = null;

    /**
     * Constructor for the controller for the Map object
     * @param context Since the map is in MainActivity and this is the MapController, the only
     *                context will be MainActivity so it's fine to store it once and for all in the
     *                controller instead of passing it to every method call.
     */
    private MapController(Context context) {
        this.context = context;
        getLocationPermission();
    }


    /**
     * Method to get the sole instance for Singleton design pattern
     * @param context Since the map is in MainActivity and this is the MapController, the only
     *                context will be MainActivity so it's fine to store it once and for all in the
     *                controller instead of passing it to every method call.
     * @return MapController
     */
    public static MapController getInstance(Context context) {
        if (instance == null) {
            instance = new MapController(context);
        }

        return instance;
    }

    /**
     * Sets the onMarkerDragStart, onMarkerDrag and onMarkerDragEnd listeners
     */
    private void setListeners() {
        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDrag(@NonNull Marker marker) {
                // Perhaps show live coordinates
            }

            @Override
            public void onMarkerDragEnd(@NonNull Marker marker) {
                Controller.getInstance(context).updateModel(marker);
            }

            @Override
            public void onMarkerDragStart(@NonNull Marker marker) {
                // Move UI elements out of the way
            }
        });
    }

    /**
     * Defines the map. Has to be done before using the map features from the map controller
     * @param googleMap GoogleMap instance
     */
    public void setMap(GoogleMap googleMap) {
        this.googleMap = googleMap;
        requestLastKnownLocation();
        setListeners();
    }

    /**
     * Returns the selected marker on the map or null if it doesn't exist
     * @return Marker
     */
    public Marker getSelectedMarker() {
        Log.d("selectedMarker", "Attempted to get selectedMarker" + selectedMarker);
        return selectedMarker;
    }

    /**
     * Returns the LatLng for the location on the map corresponding to the center of the screen.
     * This would be the expected behaviour for a user when adding a marker on a map.
     * @return LatLng
     */
    public LatLng getCurrentScreenLocation() {
        return googleMap.getProjection().getVisibleRegion().latLngBounds.getCenter();
    }

    /**
     * Returns the LatLng of the lastKnownLocation
     * @return LatLng
     */
    @NonNull
    public LatLng getCurrentLocation() {
        return new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
    }

    /**
     * Converts a drawable vector resource into a bitmap descriptor. Used to be able to change default
     * marker icon.
     * @param vectorResId The drawable vector resource
     * @return BitmapDescriptor
     */
    @NonNull
    private BitmapDescriptor bitmapDescriptorFromVector(int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        assert vectorDrawable != null;
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
//        Bitmap bitmap = Bitmap.createScaledBitmap(
//                Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
//                        vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888),
//                50, 50,
//                true
//        );

//        Canvas canvas = new Canvas(bitmap);
        Matrix matrix = new Matrix();
        matrix.postScale(.9F, .9F);
        Canvas canvas = new Canvas(bitmap);


        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    /**
     * Attempt at making a method that accepts a drawable vector resource and returns a resized Bitmap
     * Method is either incomplete or broken. Do not use
     * @return Bitmap
     */
    @Deprecated
    private Bitmap resizeMapIcon() {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, R.drawable.plant_icon);
        assert vectorDrawable != null;
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Bitmap imageBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.plant_icon);
        return Bitmap.createScaledBitmap(imageBitmap, 50, 50, false);
    }

    /**
     * Adds a marker to the map and returns it. Since Models were implemented and thanks to the
     * addMapMarker(Model model) overload, this method doesn't have to be public since the other one
     * is more convenient anyways.
     * @param latLng Position of the marker
     * @param title Label of the marker (which will show when selected)
     * @param resourceId Icon resource
     * @return Marker
     */
    private Marker addMapMarker(LatLng latLng, String title, int resourceId) {
        MarkerOptions markerOptions = new MarkerOptions();

        markerOptions.position(latLng);

        markerOptions.title(title);

        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

        Marker marker = Objects.requireNonNull(googleMap.addMarker(markerOptions));
        marker.setDraggable(true);

        //        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.plant_icon));

        marker.setIcon(bitmapDescriptorFromVector(resourceId));
        return marker;
    }

    /**
     * Move to current location (uses lastKnownLocation)
     */
    public void moveToCurrentLocation() {
        if (lastKnownLocation == null) {
            return;
        }

        moveCamera(getCurrentLocation());
    }

    /**
     * Animates the camera to the specified location and zoom level
     * @param latLng Location
     * @param zoom Zoom level
     */
    public void moveCamera(LatLng latLng, int zoom) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    /**
     * Animates the camera to the specified location. Zoom level at 30
     * @param latLng Location
     */
    public void moveCamera(LatLng latLng) {
        moveCamera(latLng, 30);
    }

    /**
     * Defines the lastKnownLocation variable if possible. Called in the setMap method only for now.
     */
    public void requestLastKnownLocation() {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

        try {
            if (locationPermissionGranted) {

                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener((Activity) context, task -> {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.getResult();
                        moveToCurrentLocation();

                    } else {
                        Log.d("INFO", "Current location is null. Using defaults.");
                        Log.e("INFO", "Exception: %s", task.getException());
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    /**
     * Requests the location permission.
     */
    public void getLocationPermission() {
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

    /**
     * Adds a polygon to the map
     * @param rectOptions PolygonOptions type
     * @return Polygon
     */
    public Polygon addPolygon(PolygonOptions rectOptions) {
        return googleMap.addPolygon(rectOptions);
    }

    /**
     * Adds a marker to the map and returns it. Gets all the necessary info from the Model object
     * @param model Any subtype of Model
     * @return Marker
     */
    public Marker addMapMarker(Model model) {
        return addMapMarker(model.getLatLng(), model.getLabel(), model.getResourceId());
    }

    /**
     * Updates the marker's information excepted for its location.
     * @param marker Marker object
     * @param label Marker label
     * @param draggable Whether the Marker is allowed to be dragged around
     */
    public void updateMarker(Marker marker, String label, boolean draggable) {
        marker.setTitle(label);
        marker.setDraggable(draggable);
    }

    /**
     * Defines the setOnMarkerClickListener listener. The method content can't be added to the
     * MainActivity.this.setListeners method because we need the googleMap instance, and it's more
     * self-explanatory to make a separate method for this, even though the setting up of the
     * MapController gets more complex because whichever method needs to be called and when is not
     * exactly clear from a new dev perspective. On the other hand, condensing all method calls
     * into one is obviously not a solution either.
     * @param consumer Consumer type that will be called when the listener gets triggered
     *                 (when the marker is clicked)
     */
    public void setOnMarkerClickListener(Consumer<Marker> consumer) {
        googleMap.setOnMarkerClickListener(marker -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                consumer.accept(marker);
                selectedMarker = marker;
            }
            return false;
        });
    }

    /**
     * Defines the setOnMapClickListener listener.
     * @param func Custom Lambda type that accepts no arguments and returns nothing.
     */
    public void setOnMapClickListener(Lambda func) {
        googleMap.setOnMapClickListener(latLng -> {
            func.run();
            selectedMarker = null;
        });
    }
}
