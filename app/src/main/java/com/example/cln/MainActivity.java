package com.example.cln;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.DisplayCutout;
import android.view.RoundedCorner;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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
import androidx.core.app.ActivityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cln.Models.Composter;
import com.example.cln.Models.Filter;
import com.example.cln.Models.Plant;
import com.example.cln.Models.Tree;
import com.example.cln.Utils.Lambda;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
androidx.core.view.OnApplyWindowInsetsListener, AdapterView.OnItemSelectedListener,
ActivityCompat.OnRequestPermissionsResultCallback {
    private Controller controller;
    private MapController mapController;
    private Window window;
    private InputMethodManager inputMethodManager;

    private EditText txtSearch;
    private FrameLayout navView;
    private Integer foldedHeight;
    private Integer expandedHeight;
    private int navState = 0;

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

        deleteDatabase("cln.sqlite");

        // set global variables
        defineGlobals();

        ViewCompat.setOnApplyWindowInsetsListener(window.getDecorView(), this);



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

    @Override
    protected void onDestroy() {
        mapController.releaseContext();
        super.onDestroy();
    }
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

        window = getWindow();

        inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);


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

    protected void requestFocus(EditText editText) {
        editText.post(() -> {
            editText.requestFocus();
            inputMethodManager.toggleSoftInputFromWindow(editText.getWindowToken(), 0, 0);
        });
    }

    protected void clearFocus(EditText editText) {
        editText.clearFocus();
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    /**
     * Expands the navigation bar to make the newly visible UI fit
     */
    protected void expandNavView() {
        if (navState == 1) {
            return;
        }

        navState = 1;
        ObjectAnimator translateAnimator = ObjectAnimator.ofFloat(navView, "translationY",
                Math.round(txtSearch.getY() - navView.getY()+expandedHeight-foldedHeight));
        translateAnimator.setDuration(300);

        ValueAnimator scaleAnimator = ValueAnimator.ofInt(foldedHeight, expandedHeight);
        scaleAnimator.addUpdateListener(valueAnimator -> {
            int val = (Integer) valueAnimator.getAnimatedValue();
            ViewGroup.LayoutParams layoutParams = navView.getLayoutParams();
            layoutParams.height = val;
            navView.setLayoutParams(layoutParams);
        });

//        ObjectAnimator animator2 = ObjectAnimator.ofFloat(navView, "scaleY", 2f);
//        animator2.setDuration(1000);

        AnimatorSet set = new AnimatorSet();
        set.play(scaleAnimator).before(translateAnimator);
        set.start();

        foldedNavView.setVisibility(View.GONE);
        expandedNavView.setVisibility(View.VISIBLE);

//        navView.bringToFront();

//        navView.setAnimation(scaleAnimation);
//        navView.startAnimation(scaleAnimation);




//        Animation resizeAnimation = new ScaleAnimation(0, 0, 0, 2);
//        resizeAnimation.setDuration(500);


//        ResizeAnimation resizeAnimation = new ResizeAnimation(
//                navView,
//                navView.getHeight()*3,
////                ViewGroup.LayoutParams.WRAP_CONTENT,
//                navView.getHeight()
//        );

//        resizeAnimation.setDuration(300);

    }

    /**
     * Folds the navigation bar to fit the navigation buttons.
     */
    protected void foldNavView() {
        if (navState == 0) {
            return;
        }

        navState = 0;

        ObjectAnimator translateAnimator = ObjectAnimator.ofFloat(navView, "translationY",
                0);

        translateAnimator.setDuration(300);
        ValueAnimator scaleAnimator = ValueAnimator.ofInt(expandedHeight, foldedHeight);
        scaleAnimator.addUpdateListener(valueAnimator -> {
            int val = (Integer) valueAnimator.getAnimatedValue();
            ViewGroup.LayoutParams layoutParams = navView.getLayoutParams();
            layoutParams.height = val;
            navView.setLayoutParams(layoutParams);
        });

        AnimatorSet set = new AnimatorSet();
        set.play(scaleAnimator).before(translateAnimator);
        set.start();

        foldedNavView.setVisibility(View.VISIBLE);
        expandedNavView.setVisibility(View.GONE);
//        ResizeAnimation resizeAnimation = new ResizeAnimation(
//                navView,
//                navView.getHeight()/3,
////                ViewGroup.LayoutParams.WRAP_CONTENT,
//                navView.getHeight()
//        );
//
//
//        resizeAnimation.setDuration(300);
//        navView.setAnimation(resizeAnimation);
//        navView.startAnimation(resizeAnimation);
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
        foldedHeight = navView.getHeight();
        expandedHeight = foldedHeight * 3;

        findViewById(R.id.btnPlant).setOnClickListener(v -> {
            expandNavView();

            EditText txtPlantName = ((EditText) inflatedPlant.findViewById(R.id.txtPlantName));
            requestFocus(txtPlantName);

            toggleVisibility(inflatedPlant);
            Spinner spinner = inflatedPlant.findViewById(R.id.spinnerGrowthState);
            spinner.setOnItemSelectedListener(this);


            inflatedPlant.findViewById(R.id.btnOkPlant).setOnClickListener(view -> {
                MapController mapController = MapController.getInstance(MainActivity.this);
                String label = txtPlantName.getText().toString();

                int nbFeuilles = 0;

                try {
                    nbFeuilles = Integer.parseInt(((TextView) findViewById(R.id.txtNbFeuilles))
                            .getText().toString());
                } catch (NumberFormatException ignored) {}


                Plant plant = new Plant(label,
                        mapController.getCurrentScreenLocation(), spinner.getSelectedItemPosition() + 1, nbFeuilles);

                controller.addEntry(plant);

                foldNavView();
                clearFocus(txtPlantName);
            });

            inflatedPlant.findViewById(R.id.btnCancelPlant).setOnClickListener(v1 -> {
                foldNavView();
                clearFocus(txtPlantName);
            });
        });

        findViewById(R.id.btnTree).setOnClickListener(v -> {

            expandNavView();
            toggleVisibility(inflatedTree);
            EditText txtTreeName = ((EditText) inflatedTree.findViewById(R.id.txtTreeName));
            requestFocus(txtTreeName);

//            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

//            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

            inflatedTree.findViewById(R.id.btnOkTree).setOnClickListener(view -> {
                MapController mapController = MapController.getInstance(MainActivity.this);
                String label = txtTreeName.getText().toString();


                Tree tree = new Tree(label, mapController.getCurrentScreenLocation());

                controller.addEntry(tree);

                foldNavView();
                clearFocus(txtTreeName);
            });

            inflatedTree.findViewById(R.id.btnCancelTree).setOnClickListener(v1 -> {
                foldNavView();
                clearFocus(txtTreeName);
            });
        });

        findViewById(R.id.btnFilter).setOnClickListener(v -> {
            inflatedFilter.setVisibility(View.INVISIBLE);

            expandNavView();
            toggleVisibility(inflatedFilter);
            EditText txtFilterName = ((EditText) inflatedFilter.findViewById(R.id.txtFilterName));
            requestFocus(txtFilterName);

            inflatedFilter.findViewById(R.id.btnOkFilter).setOnClickListener(view -> {
                MapController mapController = MapController.getInstance(MainActivity.this);
                String label = txtFilterName.getText().toString();

                Filter filter = new Filter(label, mapController.getCurrentScreenLocation());

                controller.addEntry(filter);

                foldNavView();
                clearFocus(txtFilterName);
            });

            inflatedFilter.findViewById(R.id.btnCancelFilter).setOnClickListener(v1 -> {
                foldNavView();
                clearFocus(txtFilterName);
            });
        });

        findViewById(R.id.btnComposter).setOnClickListener(v -> {
            expandNavView();
            toggleVisibility(inflatedComposter);
            EditText txtComposterName = ((EditText) inflatedComposter.findViewById(R.id.txtComposterName));
            requestFocus(txtComposterName);

            inflatedComposter.findViewById(R.id.btnOkComposter).setOnClickListener(view -> {
                MapController mapController = MapController.getInstance(MainActivity.this);
                String label = txtComposterName.getText().toString();


                Composter composter = new Composter(label, mapController.getCurrentScreenLocation());

                controller.addEntry(composter);

                foldNavView();
                clearFocus(txtComposterName);
            });

            inflatedComposter.findViewById(R.id.btnCancelComposter).setOnClickListener(v1 -> {
                foldNavView();
                clearFocus(txtComposterName);
            });
        });
    }

    /**
     * Method that makes sure the navigation bar has rounded corners that fit the device's and that
     * the search bar isn't under the camera cutout
     *
     * @param v      The view applying window insets
     * @param insets The insets to apply
     * @return WindowInsetsCompat
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


            MaterialShapeDrawable loadingBarBackground = new MaterialShapeDrawable(fullyRoundedCorners);
            loadingBarBackground.setFillColor(ColorStateList.valueOf(color));
            findViewById(R.id.loadingBar).setBackground(loadingBarBackground);

            MaterialShapeDrawable txtSearchshapeDrawable = new MaterialShapeDrawable(fullyRoundedCorners);
            txtSearchshapeDrawable.setFillColor(ColorStateList.valueOf(color));
            findViewById(R.id.txtSearch).setBackground(txtSearchshapeDrawable);

            MaterialShapeDrawable navViewshapeDrawable = new MaterialShapeDrawable(fitDeviceCorners);
            navViewshapeDrawable.setFillColor(ColorStateList.valueOf(color));
            findViewById(R.id.navView).setBackground(navViewshapeDrawable);


            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) txtSearch.getLayoutParams();
            layoutParams.topMargin = displayCutout.getSafeInsetTop() + 25;
            txtSearch.setLayoutParams(layoutParams);
        }

        return insets;
    }


    /**
     * Method that is called once the map is ready. Repopulates the map and sets map-related listeners.
     * @param googleMap
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        setListeners();

        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_night));

        getLocationPermission();
        mapController.setMap(googleMap);

       mapController.loadHomeParcel();

        controller.retrieveEntries();

        // Opens up a update menu on marker clicked.
        mapController.setOnMarkerClickListener(m -> {
            toggleVisibility(inflatedInfo);
            expandNavView();
            ((TextView)inflatedInfo.findViewById(R.id.txtInfoLabel)).setText(
                    (CharSequence) (Objects.requireNonNull(m.getTitle()).isEmpty() ? "Untitled" : m.getTitle())
            );

            EditText txtInfoName = ((EditText) inflatedInfo.findViewById(R.id.txtInfoName));
            txtInfoName.setText(
                    (CharSequence) (Objects.requireNonNull(m.getTitle()).isEmpty() ? "Untitled" : m.getTitle())
            );
            requestFocus(txtInfoName);


            ((Switch) inflatedInfo.findViewById(R.id.switchInfoDraggable)).setChecked(
                    !m.isDraggable()
            );

            inflatedInfo.findViewById(R.id.btnInfoUpdate).setOnClickListener(v -> {
                String label = txtInfoName.getText().toString();

                boolean draggable = !((Switch)inflatedInfo.findViewById(R.id.switchInfoDraggable)).isChecked();

                controller.updateEntry(mapController.getSelectedMarker(), label, draggable);
                foldNavView();
                clearFocus(inflatedInfo.findViewById(R.id.txtInfoName));
            });

            inflatedInfo.findViewById(R.id.btnInfoCancel).setOnClickListener(v1 -> {
                foldNavView();
                clearFocus(inflatedInfo.findViewById(R.id.txtInfoName));
            });

            inflatedInfo.findViewById(R.id.btnDeleteInfo).setOnClickListener(
                    v -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Confirmer");
                        builder.setMessage("Etes-vous sûr ?");
                        builder.setPositiveButton("Oui", (dialog, which) -> {
                            controller.deleteEntry(mapController.getSelectedMarker());
                            clearFocus(inflatedInfo.findViewById(R.id.txtInfoName));
                            foldNavView();
                        });

                        builder.setNegativeButton("Non", (dialog, which) -> dialog.dismiss());

                        AlertDialog alert = builder.create();
                        alert.show();

                    }
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