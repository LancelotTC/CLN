package com.example.cln;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cln.Controllers.MapController;

public class NewTreeActivity extends AppCompatActivity {
    private MapController mapController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_tree);
    }
}
