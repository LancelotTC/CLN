package com.example.cln.View;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.DisplayCutout;
import android.view.RoundedCorner;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowInsets;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.cln.Controllers.Controller;
import com.example.cln.Controllers.MapController;
import com.example.cln.Models.Composter;
import com.example.cln.Models.Filter;
import com.example.cln.Models.Model;
import com.example.cln.Models.Plant;
import com.example.cln.Models.Tree;
import com.example.cln.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Applies all widget listeners.
 * Class exists to avoid clutter in {@link MainActivity}.
 */
public class ApplyUIListeners implements androidx.core.view.OnApplyWindowInsetsListener {
    /**
     * Context
     */
    private final Context context;

    /**
     * Controller instance
     */
    private final Controller controller;

    /**
     * MapController instance
     */
    private final MapController mapController;


    /**
     * Window instance used to get the window insets and get the corner radius of the device to
     * adapt the UI.
     */
    private final Window window;

    /**
     * Used to open or close the soft keyboard view.
     */
    private final InputMethodManager inputMethodManager;

    /**
     * Placer variable to which a new instance will be assigned everytime the user wants to place a polygon
     */
    private Placer placer;

    /**
     * Search address widget
     */
    private final EditText txtSearch;


    /**
     * Navigation bar widget
     */
    private final FrameLayout navView;

    /**
     * Height of Navigation bar when folded
     */
    private Integer foldedHeight;

    /**
     * Height of Navigation bar when expanded
     */
    private Integer expandedHeight;

    /**
     * Whether the navigation bar is folded (0) or expanded (1). Default is folded (0).
     */
    private int navState = 0;
    private final int NAV_FOLDED = 0;
    private final int NAV_EXPANDED = 1;
    private final int NAV_CHANGED = 2;

    /**
     * Sub navigation bar that shows when the navigation bar is folded.
     */
    private final LinearLayout foldedNavView;

    /**
     * Sub navigation bar that shows when the navigation bar is expanded.
     */
    private final FrameLayout expandedNavView;


    /**
     * View on which information from clicked polygon shows.
     */
    private final View inflatedArea;

    /**
     * View on which information from clicked marker shows.
     */
    private final View inflatedInfo;

    /**
     * View with which creation of a plant is possible.
     */
    private final View inflatedPlant;

    /**
     * View with which creation of a tree is possible.
     */
    private final View inflatedTree;

    /**
     * View with which creation of a filter is possible.
     */
    private final View inflatedFilter;

    /**
     * View with which creation of a composter is possible.
     */
    private final View inflatedComposter;

    /**
     * Array that stores all 5 of the aforementioned views.
     */
    private final View[] allInflated;

    /**
     * Array that stores all navViews.
     */
    private final View[] allNavViews;

    public ApplyUIListeners(Context context) {
        this.context = context;

        window = ((Activity) context).getWindow();

        ViewCompat.setOnApplyWindowInsetsListener(window.getDecorView(), this);


        controller = Controller.getInstance(context);
        mapController = MapController.getInstance(context);

        inputMethodManager = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);

        txtSearch = findViewById(R.id.txtSearch);



        navView = findViewById(R.id.navView);
        foldedNavView = findViewById(R.id.foldedNavView);
        expandedNavView = findViewById(R.id.expandedNavView);

        foldedHeight = navView.getHeight();
        expandedHeight = foldedHeight * 3;

        inflatedArea = ((ViewStub)findViewById(R.id.areaStub)).inflate();
        inflatedArea.setVisibility(View.GONE);

        inflatedInfo = ((ViewStub)findViewById(R.id.infoStub)).inflate();

        inflatedPlant = ((ViewStub)findViewById(R.id.plantStub)).inflate();

        inflatedTree = ((ViewStub)findViewById(R.id.treeStub)).inflate();

        inflatedFilter = ((ViewStub)findViewById(R.id.filterStub)).inflate();

        inflatedComposter = ((ViewStub)findViewById(R.id.composterStub)).inflate();

        allInflated = new View[] {
                inflatedInfo,
                inflatedPlant,
                inflatedTree,
                inflatedFilter,
                inflatedComposter
        };

        allNavViews = new View[] {
                inflatedArea, expandedNavView, foldedNavView
        };
    }

    private <T extends android.view.View> T findViewById(@IdRes int id) {
        return ((AppCompatActivity) context).findViewById(id);
    }


    /**
     * Sets the text of all EditText objects given to "" (empty)
     * @param editTexts EditText widgets
     */
    public void setTextEmpty(EditText... editTexts) {
        for (EditText editText : editTexts) {
            editText.setText("");
        }
    }

    /**
     * Shows the soft keyboard and gives the focus to the EditText widget that is visible.
     * @param editText
     */
    private void requestFocus(EditText editText) {
        editText.post(() -> {
            editText.requestFocus();
            inputMethodManager.toggleSoftInputFromWindow(editText.getWindowToken(), 0, 0);
        });
    }

    /**
     * Clears the focus from the EditText that had it and hides the soft keyboard
     * @param editText
     */
    private void clearFocus(EditText editText) {
        editText.clearFocus();
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    /**
     * Start scale and translate animation of the navView
     * @param newY navView's new Y (translation)
     * @param from Old height
     * @param to New height
     */
    private void startScaleTranslateAnimation(int newY, int from, int to) {
        ObjectAnimator translateAnimator = ObjectAnimator.ofFloat(navView, "translationY",
                newY);
        translateAnimator.setDuration(300);

        ValueAnimator scaleAnimator = ValueAnimator.ofInt(from, to);
        scaleAnimator.addUpdateListener(valueAnimator -> {
            int val = (Integer) valueAnimator.getAnimatedValue();
            ViewGroup.LayoutParams layoutParams = navView.getLayoutParams();
            layoutParams.height = val;
            navView.setLayoutParams(layoutParams);
        });
        AnimatorSet set = new AnimatorSet();
        set.play(scaleAnimator).before(translateAnimator);
        set.start();
    }

    /**
     * Expands the navigation bar to make the newly visible UI fit
     */
    private void expandNavView() {
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
//        navView.startScaleTranslateAnimation(scaleAnimation);




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
    private void foldNavView() {
        if (navState == NAV_FOLDED) {
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
//        navView.startScaleTranslateAnimation(resizeAnimation);
    }

    /**
     * Switches the navView state
     * @param newNavState 0 = folded, 1 = expanded, 2 = changed
     */
    private void switchNavView(int newNavState) {
        if (navState == newNavState) {
            return;
        }

        switch (newNavState) {

            case NAV_FOLDED: {
                startScaleTranslateAnimation(0, expandedHeight, foldedHeight);
                toggleNavViewsVisibility(foldedNavView);
                break;
//                foldedNavView.setVisibility(View.VISIBLE);
//                expandedNavView.setVisibility(View.GONE);
            }

            case NAV_EXPANDED: {
                startScaleTranslateAnimation(
                        Math.round(txtSearch.getY() - navView.getY()+expandedHeight-foldedHeight),
                        foldedHeight, expandedHeight
                );
                toggleNavViewsVisibility(expandedNavView);
                break;
//                foldedNavView.setVisibility(View.GONE);
//                expandedNavView.setVisibility(View.VISIBLE);
            }

            case NAV_CHANGED: {
                startScaleTranslateAnimation(0, expandedHeight, foldedHeight);

                toggleNavViewsVisibility(inflatedArea);
                Animation fadeIn = new AlphaAnimation(0, 1);
                fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
                fadeIn.setDuration(0);
                fadeIn.setDuration(300);

                Animation fadeOut = new AlphaAnimation(1, 0);
                fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
                fadeOut.setStartOffset(0);
                fadeOut.setDuration(300);

                foldedNavView.setAnimation(fadeOut);
                inflatedArea.setAnimation(fadeIn);


                break;
//                foldedNavView.setVisibility(View.GONE);
//                inflatedArea.setVisibility(View.VISIBLE);
            }
        }

        navState = newNavState;
    }

    /**
     * Makes all inflated views invisible and makes the passed in inflated View visible.
     * @param inflated
     */
    private void toggleExpandedViewsVisibility(View inflated) {
        for (View view : allInflated) {view.setVisibility(View.GONE);}
        inflated.setVisibility(View.VISIBLE);
    }

    /**
     * Sets the view visible while all others are gone.
     * @param view view to show
     */
    private void toggleNavViewsVisibility(View view) {
        for (View v : allNavViews) {v.setVisibility(View.GONE);}
        view.setVisibility(View.VISIBLE);

    }


    /**
     * Convenience function which applies all listeners
     */
    public void applyAllListeners() {
        foldedHeight = navView.getHeight();
        expandedHeight = foldedHeight * 3;
        applyTxtSearchListener();

        applyPlantListener();
        applyTreeListener();
        applyFilterListener();
        applyComposterListener();

        applyImgBtnCurrentLocationListener();
    }

    /**
     * Applies the plant related UI listeners
     */
    public void applyPlantListener() {
        findViewById(R.id.btnPlant).setOnClickListener(v -> {
            // Show the place area UI
            switchNavView(NAV_CHANGED);
            placer = new Placer(context);

            // Converts the markers to a polygon, deletes the markers and opens the information UI.
            inflatedArea.findViewById(R.id.btnPlaceArea).setOnClickListener(v1 -> {
                ArrayList<LatLng> points = placer.getLatLngs();

                if (points.size() < 3) {
                    Toast.makeText(context, "Mettez 3 points ou plus de façon à faire une aire.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                placer.createPolygon();
//                placer.placeFlagMarker();

                mapController.setOnMapClickListener((latLng) -> {
                    switchNavView(NAV_FOLDED);
                    placer.cancel();
                });


//                foldedNavView.setVisibility(View.VISIBLE);
//                inflatedArea.setVisibility(View.GONE);
//                expandNavView();
                switchNavView(NAV_EXPANDED);
                toggleExpandedViewsVisibility(inflatedPlant);


                EditText txtPlantName = inflatedPlant.findViewById(R.id.txtPlantName);
                requestFocus(txtPlantName);

                EditText txtNbFeuilles = findViewById(R.id.txtNbFeuilles);

                toggleExpandedViewsVisibility(inflatedPlant);
                Spinner spinner = inflatedPlant.findViewById(R.id.spinnerGrowthState);
                spinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) context);


                inflatedPlant.findViewById(R.id.btnOkPlant).setOnClickListener(view -> {

                    try {
                        placer.removeMarkers();
                        String label = txtPlantName.getText().toString();

                        int nbFeuilles = 0;

                        try {
                            nbFeuilles = Integer.parseInt(txtNbFeuilles.getText().toString());
                        } catch (NumberFormatException ignored) {}


                        Plant plant = new Plant(
                                label,
                                points,
                                Integer.parseInt(((EditText) findViewById(R.id.txtAmount)).getText()
                                        .toString()),

                                spinner.getSelectedItemPosition() + 1, nbFeuilles
                        );


                        controller.addEntry(plant);

//                        switchNavView(NAV_FOLDED);;
                        switchNavView(NAV_FOLDED);
                        clearFocus(txtPlantName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    setTextEmpty(txtPlantName, txtNbFeuilles);
                });

                inflatedPlant.findViewById(R.id.btnCancelPlant).setOnClickListener(v2 -> {
                    placer.removeShape();
                    switchNavView(NAV_CHANGED);

                    setTextEmpty(txtPlantName, txtNbFeuilles);

                    clearFocus(txtPlantName);
                });
            });
        });
    }

    /**
     * Applies the tree related UI listeners
     */
    public void applyTreeListener() {
        findViewById(R.id.btnTree).setOnClickListener(v -> {

            switchNavView(NAV_EXPANDED);
            toggleExpandedViewsVisibility(inflatedTree);
            EditText txtTreeName = inflatedTree.findViewById(R.id.txtTreeName);
            requestFocus(txtTreeName);

//            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

//            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

            inflatedTree.findViewById(R.id.btnOkTree).setOnClickListener(view -> {
                MapController mapController = MapController.getInstance(context);
                String label = txtTreeName.getText().toString();


                Tree tree = new Tree(label, mapController.getCurrentScreenLocation());

                controller.addEntry(tree);

                switchNavView(NAV_FOLDED);
                clearFocus(txtTreeName);
            });

            inflatedTree.findViewById(R.id.btnCancelTree).setOnClickListener(v1 -> {
                switchNavView(NAV_FOLDED);
                clearFocus(txtTreeName);
            });
        });
    }

    /**
     * Applies the filter related UI listeners
     */
    public void applyFilterListener() {
        findViewById(R.id.btnFilter).setOnClickListener(v -> {
            // Show the place area UI
            switchNavView(NAV_CHANGED);
            placer = new Placer(context);

            // Converts the markers to a polygon, deletes the markers and opens the information UI.
            inflatedArea.findViewById(R.id.btnPlaceArea).setOnClickListener(v1 -> {
                ArrayList<LatLng> points = placer.getLatLngs();

                if (points.size() < 2) {
                    Toast.makeText(context, "Mettez 2 points ou plus de façon à faire une ligne.", Toast.LENGTH_SHORT).show();
                    return;
                }

                placer.createPolyline();

                mapController.setOnMapClickListener((latLng) -> {
                    switchNavView(NAV_FOLDED);
                    placer.cancel();
                });


//                foldedNavView.setVisibility(View.VISIBLE);
//                inflatedArea.setVisibility(View.GONE);
//                expandNavView();
                switchNavView(NAV_EXPANDED);
                toggleExpandedViewsVisibility(inflatedFilter);


                EditText txtFilterName = inflatedFilter.findViewById(R.id.txtFilterName);
                requestFocus(txtFilterName);

                EditText txtNbFeuilles = findViewById(R.id.txtNbFeuilles);

                toggleExpandedViewsVisibility(inflatedFilter);


                inflatedFilter.findViewById(R.id.btnOkFilter).setOnClickListener(view -> {

                    try {
                        placer.removeMarkers();
                        String label = txtFilterName.getText().toString();

                        Filter filter = new Filter(
                                label,
                                points
                        );


                        controller.addEntry(filter);

//                        switchNavView(NAV_FOLDED);;
                        switchNavView(NAV_FOLDED);
                        clearFocus(txtFilterName);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    setTextEmpty(txtFilterName, txtNbFeuilles);
                });

                inflatedFilter.findViewById(R.id.btnCancelFilter).setOnClickListener(v2 -> {
                    placer.removeShape();
                    switchNavView(NAV_CHANGED);

                    setTextEmpty(txtFilterName, txtNbFeuilles);

                    clearFocus(txtFilterName);
                });
            });
        });

    }

    /**
     * Applies the composter related UI listeners
     */
    public void applyComposterListener() {
        findViewById(R.id.btnComposter).setOnClickListener(v -> {
            switchNavView(NAV_EXPANDED);
            toggleExpandedViewsVisibility(inflatedComposter);
            EditText txtComposterName = inflatedComposter.findViewById(R.id.txtComposterName);
            requestFocus(txtComposterName);

            inflatedComposter.findViewById(R.id.btnOkComposter).setOnClickListener(view -> {
                MapController mapController = MapController.getInstance(context);
                String label = txtComposterName.getText().toString();


                Composter composter = new Composter(label, mapController.getCurrentScreenLocation());

                controller.addEntry(composter);

                switchNavView(NAV_FOLDED);
                clearFocus(txtComposterName);
            });

            inflatedComposter.findViewById(R.id.btnCancelComposter).setOnClickListener(v1 -> {
                switchNavView(NAV_FOLDED);
                clearFocus(txtComposterName);
            });
        });

    }

    /**
     * Applies the "current location" button listener
     */
    public void applyImgBtnCurrentLocationListener() {
        findViewById(R.id.imgBtnCurrentLocation).setOnClickListener(v -> {
            mapController.requestCurrentLocation();
        });
    }

    /**
     * Applies the txtSearch listener
     */
    public void applyTxtSearchListener() {
        ((EditText)findViewById(R.id.txtSearch)).setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                controller.moveToAddress(v.getText().toString(), context);
                return true;
            }
            return false;
        });
    }

    /**
     * Applies map related listeners
     */
    public void applyMapListener() {
        mapController.setOnMarkerClickListener(m -> {

            toggleExpandedViewsVisibility(inflatedInfo);
            switchNavView(NAV_EXPANDED);
            
            ((TextView)inflatedInfo.findViewById(R.id.txtInfoLabel)).setText(
                    Objects.requireNonNull(m.getTitle()).isEmpty() ? "Sans nom" : m.getTitle()
            );

            EditText txtInfoName = inflatedInfo.findViewById(R.id.txtInfoName);
            txtInfoName.setText(
                    Objects.requireNonNull(m.getTitle()).isEmpty() ? "Sans nom" : m.getTitle()
            );
            requestFocus(txtInfoName);

            @SuppressLint("UseSwitchCompatOrMaterialCode") Switch switchInfoDraggable = inflatedInfo.findViewById(R.id.switchInfoDraggable);


            switchInfoDraggable.setChecked(
                    !m.isDraggable()
            );

            inflatedInfo.findViewById(R.id.btnInfoUpdate).setOnClickListener(v -> {
                String label = txtInfoName.getText().toString();

                boolean draggable = !switchInfoDraggable.isChecked();

                controller.updateEntry((Marker) mapController.getSelectedObject(), label, draggable);
                switchNavView(NAV_FOLDED);
                clearFocus(inflatedInfo.findViewById(R.id.txtInfoName));
            });

            inflatedInfo.findViewById(R.id.btnInfoCancel).setOnClickListener(v1 -> {
                switchNavView(NAV_FOLDED);
                clearFocus(inflatedInfo.findViewById(R.id.txtInfoName));
            });

            inflatedInfo.findViewById(R.id.btnDeleteInfo).setOnClickListener(
                    v -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Confirmer");
                        builder.setMessage("Etes-vous sûr ?");
                        builder.setPositiveButton("Oui", (dialog, which) -> {
                            controller.deleteEntry((Marker) mapController.getSelectedObject());
                            clearFocus(inflatedInfo.findViewById(R.id.txtInfoName));
                            switchNavView(NAV_FOLDED);
                        });

                        builder.setNegativeButton("Non", (dialog, which) -> dialog.dismiss());

                        AlertDialog alert = builder.create();
                        alert.show();

                    }
            );
        });

        mapController.setOnPolygonClickListener(polygon -> {
            toggleExpandedViewsVisibility(inflatedInfo);
            switchNavView(NAV_EXPANDED);

            View switchInfoDraggable = inflatedInfo.findViewById(R.id.switchInfoDraggable);
            switchInfoDraggable.setVisibility(View.GONE);

            String l = ((Model) Objects.requireNonNull(polygon.getTag())).getLabel();
            String label = Objects.requireNonNull(l).isEmpty() ? "Sans nom" : l;

            ((TextView)inflatedInfo.findViewById(R.id.txtInfoLabel)).setText(
                    label
            );

            EditText txtInfoName = inflatedInfo.findViewById(R.id.txtInfoName);
            txtInfoName.setText(
                    label
            );
            requestFocus(txtInfoName);

            inflatedInfo.findViewById(R.id.btnInfoUpdate).setOnClickListener(v -> {
                String newLabel = txtInfoName.getText().toString();
                switchInfoDraggable.setVisibility(View.VISIBLE);

                controller.updateEntry(polygon, newLabel);
                switchNavView(NAV_FOLDED);
                clearFocus(inflatedInfo.findViewById(R.id.txtInfoName));
            });

            inflatedInfo.findViewById(R.id.btnInfoCancel).setOnClickListener(v1 -> {
                switchNavView(NAV_FOLDED);
                clearFocus(inflatedInfo.findViewById(R.id.txtInfoName));
            });

            inflatedInfo.findViewById(R.id.btnDeleteInfo).setOnClickListener(
                    v -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Confirmer");
                        builder.setMessage("Etes-vous sûr ?");

                        builder.setPositiveButton("Oui", (dialog, which) -> {
                            controller.deleteEntry((Polygon) mapController.getSelectedObject());
                            clearFocus(inflatedInfo.findViewById(R.id.txtInfoName));
                            switchNavView(NAV_FOLDED);
                            switchInfoDraggable.setVisibility(View.VISIBLE);
                        });

                        builder.setNegativeButton("Non", (dialog, which) -> dialog.dismiss());

                        AlertDialog alert = builder.create();
                        alert.show();

                    }
            );
        });

        mapController.setOnPolylineClickListener(polyline -> {
            toggleExpandedViewsVisibility(inflatedInfo);
            switchNavView(NAV_EXPANDED);

            View switchInfoDraggable = inflatedInfo.findViewById(R.id.switchInfoDraggable);
            switchInfoDraggable.setVisibility(View.GONE);

            String l = ((Model) Objects.requireNonNull(polyline.getTag())).getLabel();
            String label = Objects.requireNonNull(l).isEmpty() ? "Sans nom" : l;

            ((TextView)inflatedInfo.findViewById(R.id.txtInfoLabel)).setText(
                    label
            );

            EditText txtInfoName = inflatedInfo.findViewById(R.id.txtInfoName);
            txtInfoName.setText(
                    label
            );
            requestFocus(txtInfoName);

            inflatedInfo.findViewById(R.id.btnInfoUpdate).setOnClickListener(v -> {
                String newLabel = txtInfoName.getText().toString();
                switchInfoDraggable.setVisibility(View.VISIBLE);

                controller.updateEntry(polyline, newLabel);
                switchNavView(NAV_FOLDED);
                clearFocus(inflatedInfo.findViewById(R.id.txtInfoName));
            });

            inflatedInfo.findViewById(R.id.btnInfoCancel).setOnClickListener(v1 -> {
                switchNavView(NAV_FOLDED);
                clearFocus(inflatedInfo.findViewById(R.id.txtInfoName));
            });

            inflatedInfo.findViewById(R.id.btnDeleteInfo).setOnClickListener(
                    v -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Confirmer");
                        builder.setMessage("Etes-vous sûr ?");

                        builder.setPositiveButton("Oui", (dialog, which) -> {
                            controller.deleteEntry((Polyline) mapController.getSelectedObject());
                            clearFocus(inflatedInfo.findViewById(R.id.txtInfoName));
                            switchNavView(NAV_FOLDED);
                            switchInfoDraggable.setVisibility(View.VISIBLE);
                        });

                        builder.setNegativeButton("Non", (dialog, which) -> dialog.dismiss());

                        AlertDialog alert = builder.create();
                        alert.show();

                    }
            );
        });

    }

    /**
     * Applies the onBackPressed listener
     */
    public void onBackPressed() {
        switch (navState) {
            case NAV_FOLDED: {
                ((Activity) context).finish();
                System.exit(0);
                return;
            }

            case NAV_EXPANDED: {
                if (placer != null) {
                    placer.removeShape();
                    switchNavView(NAV_CHANGED);
                    return;
                }
                switchNavView(NAV_FOLDED);
                break;
            }
            case NAV_CHANGED: {
                switchNavView(NAV_FOLDED);
                placer.cancel();
                break;
            }
        }
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

            DisplayCutout displayCutout = Objects.requireNonNull(context.getDisplay()).getCutout();

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
            Resources.Theme theme = context.getTheme();
            theme.resolveAttribute(com.google.android.material.R.attr.colorPrimary, typedValue, true);
            @ColorInt int color = typedValue.data;


            MaterialShapeDrawable txtSearchShapeDrawable = new MaterialShapeDrawable(fullyRoundedCorners);
            txtSearchShapeDrawable.setFillColor(ColorStateList.valueOf(color));
            findViewById(R.id.txtSearch).setBackground(txtSearchShapeDrawable);

            MaterialShapeDrawable loadingBarBackground = new MaterialShapeDrawable(fullyRoundedCorners);
            loadingBarBackground.setFillColor(ColorStateList.valueOf(color));
            findViewById(R.id.loadingBar).setBackground(loadingBarBackground);

            MaterialShapeDrawable iBtnCurrentLocationShapeDrawable = new MaterialShapeDrawable(fullyRoundedCorners);
            iBtnCurrentLocationShapeDrawable.setFillColor(ColorStateList.valueOf(color));
            findViewById(R.id.imgBtnCurrentLocation).setBackground(iBtnCurrentLocationShapeDrawable);

            MaterialShapeDrawable navViewShapeDrawable = new MaterialShapeDrawable(fitDeviceCorners);
            navViewShapeDrawable.setFillColor(ColorStateList.valueOf(color));
            findViewById(R.id.navView).setBackground(navViewShapeDrawable);


            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) txtSearch.getLayoutParams();
            layoutParams.topMargin = displayCutout.getSafeInsetTop() + 25;
            txtSearch.setLayoutParams(layoutParams);
        }

        return insets;
    }
}

