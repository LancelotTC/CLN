package com.example.cln;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.app.Dialog;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.DisplayCutout;
import android.view.RoundedCorner;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.cln.Controllers.Controller;
import com.example.cln.Controllers.MapController;
import com.example.cln.Models.Composter;
import com.example.cln.Models.Filter;
import com.example.cln.Models.Plant;
import com.example.cln.Models.Tree;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        androidx.core.view.OnApplyWindowInsetsListener, AdapterView.OnItemSelectedListener {
    private Controller controller;
    private MapController mapController;
    private Window window;
    private FrameLayout navView;
    private LinearLayout foldedNavView;
    private FrameLayout expandedNavView;
    private ViewStub plantStub;
    private View inflatedPlant;
    private ViewStub treeStub;
    private View inflatedTree;
    private ViewStub filterStub;
    private View inflatedFilter;
    private ViewStub composterStub;
    private View inflatedComposter;

    private View[] inflats;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // set global variables
        defineGlobals();


        // set widget listeners
//        setListener(findViewById(R.id.btnPlant), R.layout.activity_new_plant);
        setListeners();
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

    private void defineGlobals() {
        controller = Controller.getInstance(this);
        mapController = MapController.getInstance(this);
        window = getWindow();

        navView = findViewById(R.id.navView);
        foldedNavView = findViewById(R.id.foldedNavView);
        expandedNavView = findViewById(R.id.expandedNavView);

        plantStub = findViewById(R.id.plantStub);
        plantStub.setLayoutResource(R.layout.activity_new_plant);
        inflatedPlant = plantStub.inflate();

        treeStub = findViewById(R.id.treeStub);
        treeStub.setLayoutResource(R.layout.activity_new_tree);
        inflatedTree = treeStub.inflate();

        filterStub = findViewById(R.id.filterStub);
        filterStub.setLayoutResource(R.layout.activity_new_filter);
        inflatedFilter = filterStub.inflate();

        composterStub = findViewById(R.id.composterStub);
        composterStub.setLayoutResource(R.layout.activity_new_composter);
        inflatedComposter = composterStub.inflate();

        inflats = new View[] {
                inflatedPlant,
                inflatedTree,
                inflatedFilter,
                inflatedComposter
        };
    }

    protected void expandNavView() {

        ResizeAnimation resizeAnimation = new ResizeAnimation(
                navView,
                navView.getHeight()*2,
//                ViewGroup.LayoutParams.WRAP_CONTENT,
                navView.getHeight()
        );

        resizeAnimation.setDuration(300);

        foldedNavView.setVisibility(View.GONE);
        expandedNavView.setVisibility(View.VISIBLE);


//        Button btnCancel = inflated.findViewById(R.id.btnCancelPlant);
//        btnCancel.setOnClickListener(v -> foldNavView());
        navView.setAnimation(resizeAnimation);
        navView.startAnimation(resizeAnimation);
        ConstraintSet constraintSet = new ConstraintSet();
//        constraintSet.clone((ConstraintLayout) findViewById(ConstraintSet.PARENT_ID));
        constraintSet.connect(navView.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID,
                ConstraintSet.TOP, 0);
    }
    protected void foldNavView() {
        foldedNavView.setVisibility(View.VISIBLE);
        expandedNavView.setVisibility(View.GONE);

        ResizeAnimation resizeAnimation = new ResizeAnimation(
                navView,
                navView.getHeight()/2,
//                ViewGroup.LayoutParams.WRAP_CONTENT,
                navView.getHeight()
        );


        resizeAnimation.setDuration(300);
        navView.setAnimation(resizeAnimation);
        navView.startAnimation(resizeAnimation);
    }

    protected void toggleVisibility(View inflated) {
        for (View view : inflats) {
            view.setVisibility(View.INVISIBLE);
        }
        inflated.setVisibility(View.VISIBLE);
    }

    protected void setListeners() {
        findViewById(R.id.btnPlant).setOnClickListener(v -> {

            expandNavView();
            toggleVisibility(inflatedPlant);

            inflatedPlant.findViewById(R.id.btnOkPlant).setOnClickListener(view -> {
                MapController mapController = MapController.getInstance(MainActivity.this);
                String label = ((EditText)inflatedPlant.findViewById(R.id.txtPlantName)).getText().toString();
                Spinner spinner = inflatedPlant.findViewById(R.id.spinnerGrowthState);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                        R.array.plant_growth, R.layout.activity_new_plant);
//                adapter.setDropDownViewResource(R.layout.activity_new_plant);

                spinner.setAdapter(adapter);
                spinner.setOnItemSelectedListener(this);

                Plant plant = new Plant(label,
                        mapController.getCurrentLocation(), 1, 0);

                controller.addEntry(plant);

                foldNavView();

            });

            inflatedPlant.findViewById(R.id.btnCancelPlant).setOnClickListener(v1 -> {
                foldNavView();
            });
        });

        findViewById(R.id.btnTree).setOnClickListener(v -> {
            treeStub.setLayoutResource(R.layout.activity_new_tree);

            expandNavView();
            toggleVisibility(inflatedTree);

            inflatedTree.findViewById(R.id.btnOkTree).setOnClickListener(view -> {
                MapController mapController = MapController.getInstance(MainActivity.this);
                String label = ((EditText)inflatedTree.findViewById(R.id.txtTreeName)).getText().toString();


                Tree tree = new Tree(label, mapController.getCurrentLocation());

                controller.addEntry(tree);

                foldNavView();

            });

            inflatedTree.findViewById(R.id.btnCancelTree).setOnClickListener(v1 -> {
                foldNavView();
            });
        });

        findViewById(R.id.btnFilter).setOnClickListener(v -> {
            filterStub.setLayoutResource(R.layout.activity_new_filter);

            inflatedFilter.setVisibility(View.INVISIBLE);

            expandNavView();
            toggleVisibility(inflatedFilter);

            inflatedFilter.findViewById(R.id.btnOkFilter).setOnClickListener(view -> {
                MapController mapController = MapController.getInstance(MainActivity.this);
                String label = ((EditText)inflatedFilter.findViewById(R.id.txtFilterName)).getText().toString();


                Filter filter = new Filter(label, mapController.getCurrentLocation());

                controller.addEntry(filter);

                foldNavView();

            });

            inflatedFilter.findViewById(R.id.btnCancelFilter).setOnClickListener(v1 -> {
                foldNavView();
            });
        });

        findViewById(R.id.btnComposter).setOnClickListener(v -> {
            composterStub.setLayoutResource(R.layout.activity_new_composter);


            expandNavView();
            toggleVisibility(inflatedComposter);

            inflatedComposter.findViewById(R.id.btnOkComposter).setOnClickListener(view -> {
                MapController mapController = MapController.getInstance(MainActivity.this);
                String label = ((EditText)inflatedComposter.findViewById(R.id.txtComposterName)).getText().toString();


                Composter composter = new Composter(label, mapController.getCurrentLocation());

                controller.addEntry(composter);

                foldNavView();

            });

            inflatedComposter.findViewById(R.id.btnCancelComposter).setOnClickListener(v1 -> {
                foldNavView();
            });
        });
    }
    protected void setListener(LinearLayout btnMain, int targetActivityId) {
        Dialog dialog = new Dialog(this);
        CurrentDialog.setDialog(dialog);

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

            DisplayCutout displayCutout = Objects.requireNonNull(getDisplay()).getCutout();

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

            FrameLayout navView = findViewById(R.id.navView);
            EditText txtSearch = findViewById(R.id.txtSearch);

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

        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_retro));
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(@NonNull LatLng latLng) {
//                mapController.addMapMarker(latLng, "placeholder");
            }
        });

        mapController.setMap(googleMap);
//        mapController.addEntry(new Plant("Second plant", 14.65395925, -61.00704925, 1, 1));
//        mapController.moveCamera(new LatLng(14.65370598, -61.00744924), 30);

        controller.retrieveEntries();

        mapController.moveToCurrentLocation();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, (CharSequence) parent.getItemAtPosition(position), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}