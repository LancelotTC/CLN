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
import android.annotation.SuppressLint;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.cln.Controllers.Controller;
import com.example.cln.Controllers.MapController;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, androidx.core.view.OnApplyWindowInsetsListener {
    private Controller controller;
    private MapController mapController;
    private ConstraintLayout navView;
    private EditText txtSearch;
    private Window window;
    private Display display;


//    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        deleteDatabase("bdCoach.sqlite");

        // set global variables
        setGlobals();

        // set widget listeners
        setListener(findViewById(R.id.btnPlant), findViewById(R.id.btnCancelPlant),
                R.layout.activity_new_plant);

//        setListener(findViewById(R.id.btnTree), findViewById(R.id.btnCancelPlant),
//                R.layout.activity_new_tree);
//
//        setListener(findViewById(R.id.btnFilter), findViewById(R.id.btnCancelPlant),
//                R.layout.activity_new_filter);
//
//        setListener(findViewById(R.id.btnComposter), findViewById(R.id.btnCancelPlant),
//                R.layout.activity_new_composter);




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

        navView = findViewById(R.id.navView);
        txtSearch = findViewById(R.id.txtSearch);
    }

    @Deprecated
    protected void setListeners() {
        ListenerManager.setPlantListener(
                findViewById(R.id.btnPlant),
                ((EditText)findViewById(R.id.txtPlantName)).getText().toString(),
                0,
                0,
                findViewById(R.id.btnOkPlant),
                findViewById(R.id.btnCancelPlant),
                R.drawable.plant_icon,
                R.layout.activity_new_plant,
                this
        );

    }
    protected void setListener(LinearLayout btnMain, Button btnCancel, int targetActivityId) {
        Dialog dialog = new Dialog(this);

        btnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animator scale = ObjectAnimator.ofPropertyValuesHolder(v,
                        PropertyValuesHolder.ofFloat(View.SCALE_X, 1, .9f, 1),
                        PropertyValuesHolder.ofFloat(View.SCALE_Y, 1, .9f, 1)
                );
                scale.setDuration(500);
                scale.start();
                dialog.setContentView(targetActivityId);
                dialog.show();

            }
        });
//        btnCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
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

        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_default));
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
//                mapController.addMapMarker(latLng, "placeholder");
            }
        });

        mapController.setMap(googleMap);
//        mapController.addEntry(new Plant("Second plant", 14.65395925, -61.00704925, 1, 1));
        mapController.moveCamera(new LatLng(14.65370598, -61.00744924), 30);

    }

}