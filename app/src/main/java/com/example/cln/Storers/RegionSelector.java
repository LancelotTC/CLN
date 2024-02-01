package com.example.cln.Storers;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class RegionSelector {
    private Integer regionId = 0;
    private SQLiteDatabase db;
    public RegionSelector(SQLiteDatabase db) {
        this.db = db;
    }

    public List<Double[]> getNextRegion() {
        String request = "select latitude, longitude from point where commune_id = " + regionId;
        Cursor cursor = db.rawQuery(request, null);
        cursor.moveToFirst();
        List<Double[]> coords = new ArrayList<>();

        while (!cursor.isBeforeFirst()) {
            coords.add(new Double[]{cursor.getDouble(0), cursor.getDouble(1)});
        }

        cursor.close();
        return coords;
    }

    public void close() {
        db.close();
    }
}
