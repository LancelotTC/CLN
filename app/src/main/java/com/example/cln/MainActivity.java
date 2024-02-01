package com.example.cln;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Dialog;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.DisplayCutout;
import android.view.RoundedCorner;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.cln.Controllers.Controller;
import com.example.cln.Controllers.MapController;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, androidx.core.view.OnApplyWindowInsetsListener {
    private Controller controller;
    private MapController mapController;
    private ConstraintLayout navView;
    private EditText txtSearch;
    private LinearLayout btnPlant;
    private LinearLayout btnTree;
    private LinearLayout btnFilter;
    private LinearLayout btnTerrain;
    private Window window;
    private Display display;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set global variables
        setGlobals();
        // set widget listeners
        setListeners();


        ViewCompat.setOnApplyWindowInsetsListener(window.getDecorView(), this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragmentMap);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        // Make notification bar disappear
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE);

    }

    protected void setGlobals() {
        controller = Controller.getInstance();
        mapController = MapController.getInstance(this);
        window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            display = getDisplay();
        }
        btnPlant = findViewById(R.id.btnPlant);
        btnTree = findViewById(R.id.btnTree);
        btnFilter = findViewById(R.id.btnFilter);
        btnTerrain = findViewById(R.id.btnTerrain);

        navView = findViewById(R.id.navView);
        txtSearch = findViewById(R.id.txtSearch);
    }

    protected void setListeners() {
        HashMap<LinearLayout, Integer[]> dict = new HashMap<LinearLayout, Integer[]>();
        dict.put(btnPlant, new Integer[]{R.layout.activity_new_plant, R.drawable.plant_icon});
        dict.put(btnTree, new Integer[]{R.layout.activity_new_plant, R.drawable.tree_icon});
        dict.put(btnFilter, new Integer[]{R.layout.activity_new_plant, R.drawable.filter_icon});
        dict.put(btnTerrain, new Integer[]{R.layout.activity_new_plant, R.drawable.terrain_icon});

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            dict.forEach((LinearLayout layout, Integer[] ids) -> layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    startActivity(new Intent(MainActivity.this, NewPlantActivity.class));
                    Animator scale = ObjectAnimator.ofPropertyValuesHolder(v,
                            PropertyValuesHolder.ofFloat(View.SCALE_X, 1, .9f, 1),
                            PropertyValuesHolder.ofFloat(View.SCALE_Y, 1, .9f, 1)
                    );
                    scale.setDuration(500);
                    scale.start();

                    mapController.addMapMarkerToCurrentLocation("Test", ids[1]);

                    Dialog dialog = new Dialog(MainActivity.this);
                    dialog.setContentView(ids[0]);
                    dialog.show();

                    dialog.findViewById(R.id.btnCancelPlant).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }
            }));
        }
    }


    @NonNull
    @Override
    public WindowInsetsCompat onApplyWindowInsets(@NonNull View v, @NonNull WindowInsetsCompat insets) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            WindowInsets windowInsets = window.getDecorView().getRootWindowInsets();

            RoundedCorner roundedCorners = windowInsets.getRoundedCorner(RoundedCorner.POSITION_TOP_LEFT);
            DisplayCutout displayCutout = display.getCutout();

            assert roundedCorners != null;
            assert displayCutout != null;
            ShapeAppearanceModel fullyRoundedCorners = new ShapeAppearanceModel().toBuilder()
                    .setAllCorners(CornerFamily.ROUNDED, 99999)
                    .build();


//            navView.setBackgroundColor(Color.parseColor("#FFFFFF"));
            ShapeAppearanceModel fitDeviceCorners = new ShapeAppearanceModel().toBuilder()
                    .setAllCorners(CornerFamily.ROUNDED, roundedCorners.getRadius() - 50)
                    .build();

            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = this.getTheme();
            theme.resolveAttribute(com.google.android.material.R.attr.colorPrimary, typedValue, true);
            @ColorInt int color = typedValue.data;


            MaterialShapeDrawable txtSearchshapeDrawable = new MaterialShapeDrawable(fullyRoundedCorners);
            MaterialShapeDrawable navViewshapeDrawable = new MaterialShapeDrawable(fitDeviceCorners);
            txtSearchshapeDrawable.setFillColor(ColorStateList.valueOf(color));
            navViewshapeDrawable.setFillColor(ColorStateList.valueOf(color));



            txtSearch.setBackground(txtSearchshapeDrawable);
            navView.setBackground(navViewshapeDrawable);

            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) txtSearch.getLayoutParams();
            layoutParams.topMargin = displayCutout.getSafeInsetTop() + 25;
            txtSearch.setLayoutParams(layoutParams);

        }

        return insets;
    }

    /**
     * Prompts the user for permission to use the device location.
     */


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_night));
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
//                mapController.addMapMarker(latLng, "placeholder");
            }
        });

        mapController.setMap(googleMap);

//        mapController.drawBoundaries();


// Get back the mutable Polygon
        PolygonOptions rectOptions = new PolygonOptions();

        ArrayList<LatLng> arrayList = new ArrayList<>();

        arrayList.add(new LatLng(-60.99824762,14.61682307));
        arrayList.add(new LatLng(-60.99826014,14.61682354));
        arrayList.add(new LatLng(-60.99831924,14.61682594));
        arrayList.add(new LatLng(-60.99836609,14.61682779));
        arrayList.add(new LatLng(-60.9983562,14.61689413));
        arrayList.add(new LatLng(-60.99825559,14.61692616));
        arrayList.add(new LatLng(-60.99824762,14.61682307));
        rectOptions.addAll(arrayList);

//        for (Double[] doubles : arrayList) {
////            mapController.addMapMarker(doubles[0], doubles[1], "test", R.drawable.plant_icon);
//            Polygon polygon = mapController.addPolygon(rectOptions);
//
//        }

        mapController.addPolygon(rectOptions);


//        mapController.moveCamera(new LatLng(5.37853969, 45.86960575), 30);
        mapController.moveCamera(new LatLng(-60.99824762, 14.61682307), 30);

    }

}