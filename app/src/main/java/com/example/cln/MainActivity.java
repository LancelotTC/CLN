package com.example.cln;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.DisplayCutout;
import android.view.RoundedCorner;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        androidx.core.view.OnApplyWindowInsetsListener, AdapterView.OnItemSelectedListener {
    private Controller controller;
    private MapController mapController;
    private Window window;

    private EditText txtSearch;
    private FrameLayout navView;
    private LinearLayout foldedNavView;
    private FrameLayout expandedNavView;

    private View inflatedInfo;

    private View inflatedPlant;
    private View inflatedTree;
    private View inflatedFilter;
    private View inflatedComposter;

    private View[] allInflated;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // set global variables
        defineGlobals();

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


    /**
     * Defines all global variables in MainActivity
     */
    private void defineGlobals() {
        controller = Controller.getInstance(this);
        mapController = MapController.getInstance(this);

        window = getWindow();

        txtSearch = findViewById(R.id.txtSearch);

        navView = findViewById(R.id.navView);
        foldedNavView = findViewById(R.id.foldedNavView);
        expandedNavView = findViewById(R.id.expandedNavView);

        inflatedInfo = ((ViewStub)findViewById(R.id.infoStub)).inflate();

        inflatedPlant = ((ViewStub)findViewById(R.id.plantStub)).inflate();

        inflatedTree = ((ViewStub)findViewById(R.id.treeStub)).inflate();

        inflatedFilter = ((ViewStub)findViewById(R.id.filterStub)).inflate();

        inflatedComposter = ((ViewStub)findViewById(R.id.composterStub)).inflate();

        allInflated = new View[] {
                inflatedPlant,
                inflatedTree,
                inflatedFilter,
                inflatedComposter
        };
    }

    /**
     * Expands the navigation bar to make the newly visible UI fit
     */
    protected void expandNavView() {

        AnimationSet animationSet = new AnimationSet(true);

        Animation translateAnimation = new TranslateAnimation(0, 0, 0,
                Math.round(txtSearch.getY() - navView.getY()));
        translateAnimation.setDuration(300);
        animationSet.addAnimation(translateAnimation);


        Animation resizeAnimation = new ScaleAnimation(1, 1, 1, 2);
        resizeAnimation.setDuration(300);
        animationSet.addAnimation(resizeAnimation);

        animationSet.setInterpolator(new Interpolator() {
            @Override
            public float getInterpolation(float input) {
                return 0;
            }
        });


//        ResizeAnimation resizeAnimation = new ResizeAnimation(
//                navView,
//                navView.getHeight()*3,
////                ViewGroup.LayoutParams.WRAP_CONTENT,
//                navView.getHeight()
//        );

//        resizeAnimation.setDuration(300);

        foldedNavView.setVisibility(View.GONE);
        expandedNavView.setVisibility(View.VISIBLE);


//        Button btnCancel = inflated.findViewById(R.id.btnCancelPlant);
//        btnCancel.setOnClickListener(v -> foldNavView());
        navView.setAnimation(animationSet);
        navView.startAnimation(animationSet);

    }

    /**
     * Folds the navigation bar to fit the navigation buttons.
     */
    protected void foldNavView() {
        foldedNavView.setVisibility(View.VISIBLE);
        expandedNavView.setVisibility(View.GONE);

        ResizeAnimation resizeAnimation = new ResizeAnimation(
                navView,
                navView.getHeight()/3,
//                ViewGroup.LayoutParams.WRAP_CONTENT,
                navView.getHeight()
        );


        resizeAnimation.setDuration(300);
        navView.setAnimation(resizeAnimation);
        navView.startAnimation(resizeAnimation);
    }

    /**
     * Makes all inflated views invisible and makes the passed in inflated View visible.
     * @param inflated
     */
    protected void toggleVisibility(View inflated) {
        for (View view : allInflated) {
            view.setVisibility(View.INVISIBLE);
        }
        inflated.setVisibility(View.VISIBLE);
    }

    /**
     * Sets (almost) all UI related listeners, mainly buttons
     */
    protected void setListeners() {
        findViewById(R.id.btnPlant).setOnClickListener(v -> {

            expandNavView();
            toggleVisibility(inflatedPlant);
            Spinner spinner = inflatedPlant.findViewById(R.id.spinnerGrowthState);
            spinner.setOnItemSelectedListener(this);


            inflatedPlant.findViewById(R.id.btnOkPlant).setOnClickListener(view -> {
                MapController mapController = MapController.getInstance(MainActivity.this);
                String label = ((EditText)inflatedPlant.findViewById(R.id.txtPlantName)).getText().toString();

                int nbFeuilles = 0;

                try {
                    nbFeuilles = Integer.parseInt(((TextView) findViewById(R.id.txtNbFeuilles))
                            .getText().toString());
                } catch (NumberFormatException ignored) {}


                Plant plant = new Plant(label,
                        mapController.getCurrentScreenLocation(), spinner.getSelectedItemPosition(), nbFeuilles);

                controller.addEntry(plant);

                foldNavView();

            });

            inflatedPlant.findViewById(R.id.btnCancelPlant).setOnClickListener(v1 -> {
                foldNavView();
            });
        });

        findViewById(R.id.btnTree).setOnClickListener(v -> {

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

        inflatedInfo.findViewById(R.id.btnInfoUpdate).setOnClickListener(v -> {
            String label = ((EditText)inflatedInfo.findViewById(R.id.txtInfoName)).getText().toString();

            boolean draggable = !((Switch)inflatedInfo.findViewById(R.id.switchInfoDraggable)).isChecked();


            controller.updateEntry(mapController.getSelectedMarker(), label, draggable);
            foldNavView();

        });

        inflatedInfo.findViewById(R.id.btnInfoCancel).setOnClickListener(v -> {
            foldNavView();
        });
    }

    /**
     * Method that makes sure the navigation bar has rounded corners that fit the device's and that
     * the search bar isn't under the camera cutout
     *
     * @param v      The view applying window insets
     * @param insets The insets to apply
     * @return
     */
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

    /**
     * Method that is called once the map is ready. Repopulates the map and sets map-related listeners.
     * @param googleMap
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        setListeners();

        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_night));

        mapController.setMap(googleMap);

        controller.retrieveEntries();

        mapController.moveToCurrentLocation();


        // Opens up a update menu on marker clicked.
        mapController.setOnMarkerClickListener(m -> {
            toggleVisibility(inflatedInfo);
            expandNavView();
            ((TextView)inflatedInfo.findViewById(R.id.txtInfoLabel)).setText(
                    (CharSequence) (Objects.requireNonNull(m.getTitle()).isEmpty() ? "Untitled" : m.getTitle())
            );

            ((EditText)inflatedInfo.findViewById(R.id.txtInfoName)).setText(
                    (CharSequence) (Objects.requireNonNull(m.getTitle()).isEmpty() ? "Untitled" : m.getTitle())
            );

            ((Switch) inflatedInfo.findViewById(R.id.switchInfoDraggable)).setChecked(
                    !m.isDraggable()
            );
        });
        // Folds the menu back to the navigation buttons on map clicked (exits the marker update menu)
        Lambda task = this::foldNavView;
        mapController.setOnMapClickListener(task);
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
}