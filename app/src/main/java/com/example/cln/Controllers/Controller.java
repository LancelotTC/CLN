package com.example.cln.Controllers;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.example.cln.Models.Model;
import com.example.cln.Models.MultiPointModel;
import com.example.cln.Models.PointModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.Polyline;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * General controller for the entire application. Tasks that require multiple fronts to be acted upon
 * (such as updating an entry: requires both the marker and the database entry to be deleted) can be
 * done with it.
 */
public class Controller {
    /**
     * Static instance of class for Singleton design pattern
     */
    private static Controller instance;

    /**
     * Static instance of MapController
     */
    private final MapController mapController;

//    /**
//     * Static instance of LocalAccess
//     */
//    private final LocalAccess localAccess;

    /**
     * Static instance of RemoteAccess
     */
    private final RemoteAccess remoteAccess;


    /**
     * Private class constructor. Get instance with {#getInstance}
     * @param context Required to instanciate all other subcontrollers
     */
    private Controller(Context context) {
        mapController = MapController.getInstance(context);
//        localAccess = LocalAccess.getInstance(context);
        remoteAccess = RemoteAccess.getInstance(context);
    }

    /**
     * Public method to get the sole instance for Singleton design pattern
     * @param context Required to instanciate all other subcontrollers
     * @return Controller
     */
    public static Controller getInstance(Context context) {
        if (instance == null) {
            instance = new Controller(context);
        }

        return instance;
    }


    /**
     * Adds a marker to the map as well as an entry in the database.
     * @param model Any child instance of Model
     */
    public void addEntry(Model model) {
        // TODO: directly send JSON to remoteAccess and only create object in callback instead of
        // TODO: creating object twice.
        remoteAccess.add(model);
    }

    /**
     * Method that receives the result of the request.
     * @param model resulting model
     */
    public void addEntryCallback(Model model) {
        mapController.addObject(model);
    }

    /**
     * Retrieves the entry from the database asynchronously
     */
    public void retrieveEntries() {
        remoteAccess.getAll();
    }

    /**
     * Adds all retrieved models onto the map
     * @param models Array of Model
     */
    public void populateMap(Model[] models) {
        for (Model model : models) {
            mapController.addObject(model);
        }
    }

    /**
     * Updates the PointModel location associated with the marker in the database.
     * @param marker Marker associated with a Model
     */
    public void updatePointModelLocation(Marker marker) {
        if (marker.getTag() == null) {
            Log.d("Skipped update",
                    "Skipped updating location of marker because its tag was null");
            return;
        }
        PointModel pointModel = (PointModel) marker.getTag();
        pointModel.setLatLng(marker.getPosition());
        remoteAccess.update(pointModel);
    }

    /**
     * Updates the Model's label and whether the Marker should be draggable.
     * TODO: Be able to update every setting of the corresponding Model.
     * @param marker Marker associated with a Model
     * @param label The new label
     * @param draggable whether the marker should be draggable.
     */
    public void updateEntry(Marker marker, String label, boolean draggable) {
        mapController.updateMarker(marker, label, draggable);
        Model model = (Model) marker.getTag();
        assert model != null;
        model.setLabel(label);
        remoteAccess.update(model);
    }

    /**
     * Updates the Model's label
     * @param polygon The polygon which label should be modified
     * @param label The label
     */
    public void updateEntry(Polygon polygon, String label) {
        MultiPointModel multiPointModel = (MultiPointModel) polygon.getTag();
        assert multiPointModel != null;
        multiPointModel.setLabel(label);
        remoteAccess.update(multiPointModel);
    }

    /**
     * Updates the Model's label
     * @param polyline The polyline which label should be modified
     * @param label The label
     */
    public void updateEntry(Polyline polyline, String label) {
        MultiPointModel multiPointModel = (MultiPointModel) polyline.getTag();
        assert multiPointModel != null;
        multiPointModel.setLabel(label);
        remoteAccess.update(multiPointModel);
    }

    /**
     * Deletes the Model associated with the marker
     * @param selectedMarker Marker associated with a Model
     */
    public void deleteEntry(Marker selectedMarker) {
//        localAccess.deleteEntry(selectedMarker);
        remoteAccess.delete((Model) Objects.requireNonNull(selectedMarker.getTag()));
        selectedMarker.remove();
    }

    /**
     * Deletes the Model associated with the Polygon
     * @param selectedPolygon Polygon associated with a Model
     */
    public void deleteEntry(Polygon selectedPolygon) {
//        localAccess.deleteEntry(selectedMarker);
        remoteAccess.delete((Model) Objects.requireNonNull(selectedPolygon.getTag()));
        selectedPolygon.remove();
    }

    /**
     * Deletes the Model associated with the Polyline
     * @param selectedPolyline Polyline associated with a Model
     */
    public void deleteEntry(Polyline selectedPolyline) {
//        localAccess.deleteEntry(selectedMarker);
        remoteAccess.delete((Model) Objects.requireNonNull(selectedPolyline.getTag()));
        selectedPolyline.remove();
    }

    /**
     * Accepts a String containing an address and moves the camera to said address.
     * TODO: Resulting LatLng should always be within Martinique
     * @param address The address
     * @param context The context
     */
    public void moveToAddress(String address, Context context) {
        Geocoder geocoder = new Geocoder(context);
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 3);
            assert addresses != null;
            assert addresses.size() > 0;
            LatLng newLocation = new LatLng(addresses.get(0).getLatitude(), addresses.get(0).getLongitude());

//            Shortcuts.log("Address",
//                    addresses.get(0).getSubAdminArea() + " getLocality" + // Le Lamentin
//                    addresses.get(0).getPhone() + " getFeatureName" + // 119
//                    addresses.get(0).getFeatureName() + " getThoroughfare" + // Chemin Nestor
//                    addresses.get(0).getThoroughfare() + " getSubThoroughfare" + // 119
//                    addresses.get(0).getSubThoroughfare() + " getLocale"
//            );
            //            mapController.addMarker(newLocation, address, R.drawable.location_icon);
            mapController.moveCamera(newLocation);
        } catch (IOException | AssertionError e) {
            e.printStackTrace();
        }
    }
}
