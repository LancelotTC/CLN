package com.example.cln;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cln.Controllers.Controller;
import com.example.cln.Controllers.MapController;
import com.example.cln.Models.Plant;

public class NewPlantActivity extends AppCompatActivity {
//    private MapController mapController;

    private Controller controller;
    private EditText txtPlantName;
    private Spinner spinnerGrowthState;
    private EditText txtNbFeuilles;
    private Button btnOkPlant;
    private Button btnCancelPlant;
    private Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("created", "NewPlantActivity");

        setContentView(R.layout.activity_new_plant);
        setGlobals();
        ArrayAdapter<String> itemsAdapter = new ArrayAdapter<>(this, R.layout.spinner_list_layout, new String[]{
                "Petit soldat",
                "Papillon",
                "Feuilles"
        });

        spinnerGrowthState.setAdapter(itemsAdapter);
        spinnerGrowthState.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);

        setListeners();

    }


    private void setGlobals() {
        dialog = CurrentDialog.getDialog();

        controller = Controller.getInstance(this);

        txtPlantName = dialog.findViewById(R.id.txtPlantName);
        spinnerGrowthState = dialog.findViewById(R.id.spinnerGrowthState);
        txtNbFeuilles = dialog.findViewById(R.id.txtNbFeuilles);
        btnOkPlant = dialog.findViewById(R.id.btnOkPlant);
        btnCancelPlant = dialog.findViewById(R.id.btnCancelPlant);

    }

    protected void setListeners() {

        btnOkPlant.setOnClickListener(v -> {
            Log.d("Click", "clicked on ok plant");
            MapController mapController = MapController.getInstance(this);
            String label = ((EditText)findViewById(R.id.txtPlantName)).getText().toString();

            Plant plant = new Plant(label,
                    mapController.getCurrentScreenLocation(), 1, 0);

            controller.addEntry(plant);
        });
//        btnCancelPlant.setOnClickListener(v -> dialog.dismiss());
    }
}
