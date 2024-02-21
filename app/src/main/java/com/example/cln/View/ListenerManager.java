//package com.example.cln;
//
//import android.animation.Animator;
//import android.animation.ObjectAnimator;
//import android.animation.PropertyValuesHolder;
//import android.app.Activity;
//import android.app.Dialog;
//import android.content.Context;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//
//import com.example.cln.MapController;
//import com.example.cln.Models.Composter;
//import com.example.cln.Models.Filter;
//import com.example.cln.Models.ModelInterface;
//import com.example.cln.Models.Plant;
//import com.example.cln.Models.Tree;
//
//public class ListenerManager {
//
//    public static void setListener(LinearLayout btnMain, int targetActivityId, Context context) {
//
//        btnMain.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Dialog dialog = new Dialog(context);
//
//                Animator scale = ObjectAnimator.ofPropertyValuesHolder(v,
//                        PropertyValuesHolder.ofFloat(View.SCALE_X, 1, .9f, 1),
//                        PropertyValuesHolder.ofFloat(View.SCALE_Y, 1, .9f, 1)
//                );
//                scale.setDuration(500);
//                scale.start();
//
//                dialog.setContentView(targetActivityId);
//                dialog.show();
//            }
//        });
//
//    }
//    private static void setListener(LinearLayout btnMain, String label, Button btnOk, Button btnCancel,
//                                    int resourceId, int targetActivityId, Context context) {
//        Dialog dialog = new Dialog(context);
//
//        btnMain.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Animator scale = ObjectAnimator.ofPropertyValuesHolder(v,
//                        PropertyValuesHolder.ofFloat(View.SCALE_X, 1, .9f, 1),
//                        PropertyValuesHolder.ofFloat(View.SCALE_Y, 1, .9f, 1)
//                );
//                scale.setDuration(500);
//                scale.start();
//                dialog.setContentView(targetActivityId);
//
//                dialog.show();
//            }
//        });
//
//        btnCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//    }
//
//    static private void setDialogDismissListener(Button btnCancel, Dialog dialog) {
//        btnCancel.setOnClickListener(v -> dialog.dismiss());
//    }
//    static public void setPlantListener(int txtNameId, Integer growthState, Integer leafAmount,
//    Button btnOk, Button btnCancel, int resourceId, Dialog dialog, Context context) {
//        setDialogDismissListener(btnCancel, dialog);
//
//        btnOk.setOnClickListener(v -> {
//            MapController mapController = MapController.getInstance(context);
//            String label = R.id.txtPlantName.getText().toString();
//
//            Plant plant = new Plant(label,
//                    mapController.getCurrentScreenLocation(), growthState, leafAmount);
//            mapController.addMarker(mapController.getCurrentScreenLocation(), label, resourceId);
//            // Store the plant in db
//        });
//
//    }
//
//    static public void setTreeListener(LinearLayout btnMain, String label, Button btnOk, Button btnCancel,
//    int resourceId, int targetActivityId, Context context) {
//        setListener(btnMain, label, btnOk, btnCancel,
//                resourceId, targetActivityId, context);
//
//        btnOk.setOnClickListener(v -> {
//            Tree tree = new Tree(label,
//                    MapController.getInstance(context).getCurrentLocation());
//            // More
//        });
//    }
//
//    public static void setFilterListener(LinearLayout btnMain, String label, Button btnOk,
//    Button btnCancel, int resourceId, int targetActivityId, Context context) {
//        setListener(btnMain, label, btnOk, btnCancel,
//                resourceId, targetActivityId, context);
//
//        btnOk.setOnClickListener(v -> {
//            Filter filter = new Filter(label,
//                    MapController.getInstance(context).getCurrentLocation());
//            // More
//        });
//    }
//    static public void setComposterListener(LinearLayout btnMain, String label, Button btnOk,
//    Button btnCancel, int resourceId, int targetActivityId, Context context) {
//        setListener(btnMain, label, btnOk, btnCancel,
//                resourceId, targetActivityId, context);
//
//        btnOk.setOnClickListener(v -> {
//            Composter composter = new Composter(label,
//                    MapController.getInstance(context).getCurrentLocation());
//            // more
//        });
//    }
//}
