package com.example.cln;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cln.Controllers.MapController;
import com.example.cln.Models.Plant;

public class NewPlantActivity extends AppCompatActivity {
    private MapController mapController;

    private EditText txtPlantName;
    private Spinner spinnerGrowthState;
    private EditText txtNbFeuilles;
    private Button btnOk;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        txtPlantName = findViewById(R.id.txtPlantName);
        spinnerGrowthState = findViewById(R.id.spinnerGrowthState);
        txtNbFeuilles = findViewById(R.id.txtNbFeuilles);
        btnOk = findViewById(R.id.btnCancelPlant);

    }

    protected void setListeners() {
        btnOk.setOnClickListener(v -> {
            MapController mapController = MapController.getInstance(this);
            Plant plant = new Plant(txtPlantName.getText().toString(),
                    mapController.getCurrentScreenLocation(), 1,
                    Integer.parseInt(txtNbFeuilles.getText().toString()));
            mapController.addMapMarker(mapController.getCurrentScreenLocation(),
                    txtPlantName.getText().toString(), R.drawable.plant_icon);
        });
    }
}
