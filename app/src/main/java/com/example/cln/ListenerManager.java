package com.example.cln;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.cln.Controllers.MapController;
import com.example.cln.Models.Composter;
import com.example.cln.Models.Filter;
import com.example.cln.Models.Plant;
import com.example.cln.Models.Tree;

public class ListenerManager {
    private static void setListener(LinearLayout btnMain, String label, Button btnOk, Button btnCancel,
                                    int resourceId, int targetActivityId, Context context) {
        Dialog dialog = new Dialog(context);

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

                MapController.getInstance(context)
                        .addMapMarkerToCurrentLocation(label, resourceId);

                dialog.show();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
    static public void setPlantListener(LinearLayout btnMain, String label, Integer growthState, Integer leafAmount,
    Button btnOk, Button btnCancel,int resourceId, int targetActivityId, Context context) {
        setListener(btnMain, label, btnOk, btnCancel, resourceId, targetActivityId, context);

        btnOk.setOnClickListener(v -> {
            MapController mapController = MapController.getInstance(context);
            Plant plant = new Plant(label,
                    mapController.getCurrentScreenLocation(), growthState, leafAmount);
            mapController.addMapMarker(mapController.getCurrentScreenLocation(), label, resourceId);
        });
    }

    static public void setTreeListener(LinearLayout btnMain, String label, Button btnOk, Button btnCancel,
    int resourceId, int targetActivityId, Context context) {
        setListener(btnMain, label, btnOk, btnCancel,
                resourceId, targetActivityId, context);

        btnOk.setOnClickListener(v -> {
            Tree tree = new Tree(label,
                    MapController.getInstance(context).getCurrentLocation());
            // More
        });
    }

    public static void setFilterListener(LinearLayout btnMain, String label, Button btnOk,
    Button btnCancel, int resourceId, int targetActivityId, Context context) {
        setListener(btnMain, label, btnOk, btnCancel,
                resourceId, targetActivityId, context);

        btnOk.setOnClickListener(v -> {
            Filter filter = new Filter(label,
                    MapController.getInstance(context).getCurrentLocation());
            // More
        });
    }
    static public void setComposterListener(LinearLayout btnMain, String label, Button btnOk,
    Button btnCancel, int resourceId, int targetActivityId, Context context) {
        setListener(btnMain, label, btnOk, btnCancel,
                resourceId, targetActivityId, context);

        btnOk.setOnClickListener(v -> {
            Composter composter = new Composter(label,
                    MapController.getInstance(context).getCurrentLocation());
            // more
        });
    }
}
