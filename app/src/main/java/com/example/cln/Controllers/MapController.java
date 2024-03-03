package com.example.cln.Controllers;

//import static androidx.appcompat.graphics.drawable.DrawableContainerCompat.Api21Impl.getResources;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.cln.Models.IArea;
import com.example.cln.Models.ILine;
import com.example.cln.Models.Model;
import com.example.cln.Models.MultiPointModel;
import com.example.cln.Models.PointModel;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
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
    @SuppressLint("StaticFieldLeak")
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
     * WARNING: This, coupled with the instance of MapController being static, constitutes a memory
     * leak. I've set the onDestroy method in the activity that calls {@link #releaseContext()} method
     * that clears the reference. I'm banking on the fact that this is a sufficient solution and that
     * phones nowadays know how to manage such memory leaks if it every appears in the first place.
     * Since the map is in MainActivity and this is the MapController, the only context will be
     * MainActivity so it's fine to store it once and for all in the controller instead of passing
     * it to every method call
     */
    private Context context;

    /**
     * Stores the selected marker on the map or null if it doesn't exist
     */
    private Object selectedObject = null;

    /**
     * Maximum zoom allowed by the Google Map API depending on the map
     */
    private final int MAX_ZOOM;

    private boolean objectsClickable = true;

    {
        MAX_ZOOM = 21;
    }


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
     * Public method to get the sole instance for Singleton design pattern
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
                Controller.getInstance(context).updatePointModelLocation(marker);
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
//        requestLastKnownLocation();
        requestCurrentLocation();
        setListeners();
    }


    /**
     * Requests the location permission.
     */
    public void getLocationPermission() {

        if (// If neither coarse and fine location perms aren't granted
//                ActivityCompat.checkSelfPermission(context,
//                        Manifest.permission.ACCESS_FINE_LOCATION)
//                        !=
//                        PackageManager.PERMISSION_GRANTED
//
//                        &&

                        ActivityCompat.checkSelfPermission(context,
                                Manifest.permission.ACCESS_COARSE_LOCATION)
                                !=
                                PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling ActivityCompat#requestPermissions here to request the missing
            //  permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
//            ActivityCompat.requestPermissions((Activity) context,
//                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
//                    1);
        } else { // If they're both granted
            locationPermissionGranted = true;
        }
    }

    /**
     * Returns the selected marker on the map or null if it doesn't exist
     * @return Marker
     */
    public Object getSelectedObject() {
        Log.d("selectedMarker", "Attempted to get selectedMarker" + selectedObject);
        return selectedObject;
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
     * Returns the LatLng for the location on the map corresponding to the center of the screen.
     * This would be the expected behaviour for a user when adding a marker on a map.
     * @return LatLng
     */
    public LatLng getCurrentScreenLocation() {
        return googleMap.getProjection().getVisibleRegion().latLngBounds.getCenter();
    }

    /**
     * Move to current location (uses lastKnownLocation)
     */
    public void moveToCurrentLocation() {
        if (lastKnownLocation == null) {
            return;
        }

        moveCamera(getCurrentLocation(), (int) googleMap.getMaxZoomLevel());
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
     * Animates the camera to the specified location. Zoom level at MAX_ZOOM (21.0)
     * @param latLng Location
     */
    public void moveCamera(LatLng latLng) {
        moveCamera(latLng, MAX_ZOOM);
    }

    private void onLastKnownLocationAssigned() {
        if (lastKnownLocation == null) {
            requestCurrentLocation();
            return;
        }
        moveToCurrentLocation();
    }

    private void onCurrentLocationAssigned() {
        if (lastKnownLocation == null) {
            return;
        }
        moveToCurrentLocation();
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
                        onLastKnownLocationAssigned();

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

    @SuppressLint("MissingPermission")
    public void requestCurrentLocation() {
        if (!locationPermissionGranted) {
            return;
        }

        LocationManager locationManager =
                (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            locationManager.getCurrentLocation(LocationManager.GPS_PROVIDER, null, command -> {}, l -> {
            });
        }
        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                lastKnownLocation = location;
                onCurrentLocationAssigned();
                locationManager.removeUpdates(this);
            }
        }, null);

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
     * Converts dp to pixels
     * @param dp Dp number
     * @param context context
     * @return the amount of pixels corresponding to the dp.
     */
    private static float convertDpToPixel(float dp, Context context){
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    private BitmapDescriptor getBitmapDescriptor(int id) {
        Drawable vectorDrawable = context.getDrawable(id);


        int h = ((int) convertDpToPixel(42, context));
        int w = ((int) convertDpToPixel(25, context));
        assert vectorDrawable != null;
        vectorDrawable.setBounds(0, 0, w, h);

        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bm);
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
     * addMarker(Model model) overload, this method doesn't have to be public since the other one
     * is more convenient anyways.
     * @param latLng Position of the marker
     * @param title Label of the marker (which will show when selected)
     * @param resourceId Icon resource
     * @return Marker
     */
    @NonNull
    public Marker addMarker(LatLng latLng, String title, int resourceId) {
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
     * Adds a marker to the map and returns it. Gets all the necessary info from the Model object
     * @param pointModel PointModel
     * @return Marker
     */
    public Marker addMarker(PointModel pointModel) {
        return addMarker(pointModel.getLatLng(), pointModel.getLabel(), pointModel.getResourceId());
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
     * Adds a polygon to the map
     * @param polygonOptions PolygonOptions type
     * @return The Polygon that was added to the map
     */
    public Polygon addPolygon(PolygonOptions polygonOptions) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(com.google.android.material.R.attr.colorPrimary, typedValue, true);
        @ColorInt int color = typedValue.data;

        polygonOptions.fillColor(color);
        polygonOptions.fillColor(color);
        polygonOptions.clickable(true);
        return googleMap.addPolygon(polygonOptions);
    }

    /**
     * Adds a polygon to the map
     * @param multiPointModel
     * @return The polygon that was added to the map
     */
    public Polygon addPolygon(MultiPointModel multiPointModel) {
        PolygonOptions polygonOptions = new PolygonOptions();
        polygonOptions.addAll(multiPointModel.getLatLngs());
        return addPolygon(polygonOptions);
    }

    /**
     * Adds a polyline to the map
     * @param polylineOptions PolylineOptions type
     * @return The Polyline that was added to the map
     */
    public Polyline addPolyline(PolylineOptions polylineOptions) {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(com.google.android.material.R.attr.colorPrimary, typedValue, true);
        @ColorInt int color = typedValue.data;


        polylineOptions.color(color);
        polylineOptions.width(25);
        polylineOptions.clickable(true);
        return googleMap.addPolyline(polylineOptions);
    }

    /**
     * Adds a polyline to the map
     * @param polyline MultiPointModel type
     * @return The Polyline that was added to the map
     */
    public Polyline addPolyline(MultiPointModel polyline) {
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.addAll(polyline.getLatLngs());
        return addPolyline(polylineOptions);
    }

    /**
     * Defines the setOnMapClickListener listener.
     * @param func - Consumer that accepts a LatLng.
     */
    public void setOnMapClickListener(Consumer<LatLng> func) {
        googleMap.setOnMapClickListener(latLng -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                func.accept(latLng);
                selectedObject = null;
            }
        });
        // TODO: reimplement this function
    }
    /**
     * Defines the setOnMarkerClickListener listener.
     * @param consumer Consumer that accepts a Marker
     */
    public void setOnMarkerClickListener(Consumer<Marker> consumer) {
        googleMap.setOnMarkerClickListener(marker -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && objectsClickable) {
                consumer.accept(marker);
                selectedObject = marker;
            }
            return false;
        });
    }

    /**
     * Defines the setOnPolygonClickListener listener.
     * @param func Consumer that accepts a Polygon.
     */
    public void setOnPolygonClickListener(Consumer<Polygon> func) {
        googleMap.setOnPolygonClickListener(polygon -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && objectsClickable) {
                func.accept(polygon);
                selectedObject = polygon;
            }



//            Shortcuts.toast(context, polygon.getPoints());
//            ArrayList<LatLng> points = (ArrayList<LatLng>) polygon.getPoints();
//            polygon.remove();

//            PolygonOptions polygonOptions = new PolygonOptions();
//            polygonOptions.clickable(true);
//
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                points.forEach(point -> {
//                    polygonOptions.add(new LatLng(point.latitude + 1, point.longitude));
//                });
//            }

//            addPolygon(polygonOptions);

//            points.forEach();
        });
    }

    /**
     * Defines the setOnPolylineClickListener listener.
     * @param func Consumer that accepts a Polyline.
     */
    public void setOnPolylineClickListener(Consumer<Polyline> func) {
        googleMap.setOnPolylineClickListener(polyline -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && objectsClickable) {
                func.accept(polyline);
                selectedObject = polyline;
            }
        });

    }


    public void releaseContext() {
        context = null;
    }

    public void loadHomeParcel() {
        PolygonOptions polygonOptions = new PolygonOptions();

        polygonOptions.add(
                new LatLng(14.65370598, -61.00744924),
                new LatLng(14.65374212, -61.00746832),
                new LatLng(14.65382301, -61.00730208),
                new LatLng(14.65419419, -61.00744231),
                new LatLng(14.65422758, -61.00737443),
                new LatLng(14.65425831, -61.00731196),
                new LatLng(14.65436187, -61.00710134),
                new LatLng(14.65395925, -61.00687656),
                new LatLng(14.65388310, -61.00704925),
                new LatLng(14.65383896, -61.00714952),
                new LatLng(14.65379287, -61.00725409),
                new LatLng(14.65375502, -61.00733992),
                new LatLng(14.65370598, -61.00744924)
        );

        polygonOptions.strokeWidth(10);
        googleMap.addPolygon(polygonOptions);

//        addMarker(new LatLng(14.65436187, -61.00710134),
//                "Maison", R.drawable.terrain_maison);

    }

    /**
     * Set whether objects sur as markers or shapes should be clickable.
     * @param objectsClickable by default true
     */
    public void setObjectsClickable(boolean objectsClickable) {
        this.objectsClickable = objectsClickable;
    }

    /**
     * Adds an object to the map, whether a Marker, a Polygon or a Polyline
     * @param model Model
     */
    public void addObject(Model model) {
        if (model instanceof PointModel) {
            Marker marker = addMarker((PointModel) model);
            marker.setTag(model);
            return;
        } else if (model instanceof MultiPointModel) {
            if (model instanceof ILine) {
                Polyline polyline = addPolyline((MultiPointModel) model);
                polyline.setTag(model);
            } else if (model instanceof IArea) {
                Polygon polygon = addPolygon((MultiPointModel) model);
                polygon.setTag(model);
            }
            return;
        }

        throw new RuntimeException("Cannot add Object of type " + model.getClass());
    }
}
