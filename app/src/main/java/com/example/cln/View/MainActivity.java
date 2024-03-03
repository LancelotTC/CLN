package com.example.cln.View;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.cln.Controllers.Controller;
import com.example.cln.Controllers.MapController;
import com.example.cln.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

/**
 * Main entry point for app
 */
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, AdapterView.OnItemSelectedListener,
ActivityCompat.OnRequestPermissionsResultCallback {
    /**
     * Controller instance
     */
    private Controller controller;

    /**
     * MapController instance
     */
    private MapController mapController;

    private ApplyUIListeners applyUIListeners;


    /**
     * Method that is called at the start of the activity.
     * Check {@link FragmentActivity#onCreate(Bundle)} for further docs.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        deleteDatabase("cln.sqlite");

        // set global variables
        defineGlobals();

//        ViewCompat.setOnApplyWindowInsetsListener(window.getDecorView(), this);



        // Make notification bar disappear
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE);

        while (true) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.fragmentMap);

            try{
                assert mapFragment != null;
            } catch (AssertionError e) {
                e.printStackTrace();
                continue;
            }

            mapFragment.getMapAsync(this);
            break;
        }
    }

    /**
     * Used to release (unreference) the context (MainActivity.this) from the mapController
     * (otherwise it can become a memory leak)
     */
    @Override
    protected void onDestroy() {
        mapController.releaseContext();
        super.onDestroy();
    }

    /**
     * Checks whether all location permissions were granted: requests current location if yes,
     * otherwise asks for them.
     */
    protected void getLocationPermission() {
        if (
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        ==
                        PackageManager.PERMISSION_DENIED
                        ||
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        ==
                        PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        } else {
//            mapController.requestLastKnownLocation();
            mapController.requestLastKnownLocation();
        }
    }

    /**
     * Callback to when permission were granted or denied. Requests the current location if granted.
     * @param requestCode The request code passed in {@link #requestPermissions(
     * android.app.Activity, String[], int)}
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     *
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
    @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        mapController.requestLastKnownLocation();
        mapController.requestLastKnownLocation();
    }

    /**
     * Defines all global variables in MainActivity
     */
    protected void defineGlobals() {
        controller = Controller.getInstance(this);
        mapController = MapController.getInstance(this);


//        window = getWindow();

        applyUIListeners = new ApplyUIListeners(this);

//
//        navView = findViewById(R.id.navView);
//        foldedNavView = findViewById(R.id.foldedNavView);
//        expandedNavView = findViewById(R.id.expandedNavView);
//
//        inflatedArea = ((ViewStub)findViewById(R.id.areaStub)).inflate();
//
//        inflatedInfo = ((ViewStub)findViewById(R.id.infoStub)).inflate();
//
//        inflatedPlant = ((ViewStub)findViewById(R.id.plantStub)).inflate();
//
//        inflatedTree = ((ViewStub)findViewById(R.id.treeStub)).inflate();
//
//        inflatedFilter = ((ViewStub)findViewById(R.id.filterStub)).inflate();
//
//        inflatedComposter = ((ViewStub)findViewById(R.id.composterStub)).inflate();
//
//        allInflated = new View[] {
//                inflatedArea,
//                inflatedInfo,
//                inflatedPlant,
//                inflatedTree,
//                inflatedFilter,
//                inflatedComposter
//        };
    }


    /**
     * Method that is called once the map is ready. Repopulates the map and sets map-related listeners.
     * @param googleMap
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        findViewById(R.id.loadingBar).setVisibility(View.GONE);

        applyUIListeners.applyAllListeners();

//        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_night));

        getLocationPermission();
        mapController.setMap(googleMap);

        mapController.loadHomeParcel();

        controller.retrieveEntries();

        // Opens up a update menu on marker clicked.
//        mapController.setOnMarkerClickListener(m -> {
//
//            toggleVisibility(inflatedInfo);
//            expandNavView();
//            ((TextView)inflatedInfo.findViewById(R.id.txtInfoLabel)).setText(
//                    (CharSequence) (Objects.requireNonNull(m.getTitle()).isEmpty() ? "Sans nom" : m.getTitle())
//            );
//
//            EditText txtInfoName = ((EditText) inflatedInfo.findViewById(R.id.txtInfoName));
//            txtInfoName.setText(
//                    (CharSequence) (Objects.requireNonNull(m.getTitle()).isEmpty() ? "Sans nom" : m.getTitle())
//            );
//            requestFocus(txtInfoName);
//
//
//            ((Switch) inflatedInfo.findViewById(R.id.switchInfoDraggable)).setChecked(
//                    !m.isDraggable()
//            );
//
//            inflatedInfo.findViewById(R.id.btnInfoUpdate).setOnClickListener(v -> {
//                String label = txtInfoName.getText().toString();
//
//                boolean draggable = !((Switch)inflatedInfo.findViewById(R.id.switchInfoDraggable)).isChecked();
//
//                controller.updateEntry(mapController.getSelectedObject(), label, draggable);
//                foldNavView();
//                clearFocus(inflatedInfo.findViewById(R.id.txtInfoName));
//            });
//
//            inflatedInfo.findViewById(R.id.btnInfoCancel).setOnClickListener(v1 -> {
//                foldNavView();
//                clearFocus(inflatedInfo.findViewById(R.id.txtInfoName));
//            });
//
//            inflatedInfo.findViewById(R.id.btnDeleteInfo).setOnClickListener(
//                    v -> {
//                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                        builder.setTitle("Confirmer");
//                        builder.setMessage("Etes-vous sûr ?");
//                        builder.setPositiveButton("Oui", (dialog, which) -> {
//                            controller.deleteEntry(mapController.getSelectedObject());
//                            clearFocus(inflatedInfo.findViewById(R.id.txtInfoName));
//                            foldNavView();
//                        });
//
//                        builder.setNegativeButton("Non", (dialog, which) -> dialog.dismiss());
//
//                        AlertDialog alert = builder.create();
//                        alert.show();
//
//                    }
//            );
//        });
        applyUIListeners.applyMapListener();

        mapController.moveCamera(new LatLng(14.65388310, -61.00704925));
        // Folds the menu back to the navigation buttons on map clicked (exits the marker update menu)
//        Lambda task = this::foldNavView;
//        mapController.setOnMapClickListener(task);

    }

    /**
     * When trying to add a Plant to the map, user has the ability to choose a plant growth state.
     * The Feuilles... choice requires the user to specify the quantity. This method allows that.
     * @param parent The AdapterView where the selection happened
     * @param view The view within the AdapterView that was clicked
     * @param position The position of the view in the adapter
     * @param id The row id of the item that is selected
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        Toast.makeText(this, (CharSequence) (position + " " + id), Toast.LENGTH_SHORT).show();
        if (position == 2) {
            findViewById(R.id.txtNbFeuilles).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.txtNbFeuilles).setVisibility(View.GONE);
        }
    }

    /**
     * Have to implement this empty method lest it won't work
     * @param parent The AdapterView that now contains no selected item.
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        applyUIListeners.onBackPressed();
    }
}