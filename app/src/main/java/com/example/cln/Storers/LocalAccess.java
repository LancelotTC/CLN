package com.example.cln.Storers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.cln.Models.ModelInterface;
import com.example.cln.Models.Plant;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Classe exploitant MySQLiteOpenHelper pour manipuler la bdd au format SQLite
 */
public class LocalAccess {

    private String dbName = "cln.sqlite";
    private Integer dbVersion = 1;
    private final MySQLiteOpenHelper dbAccess;
    private static LocalAccess instance;
    private SQLiteDatabase db;
    private Integer regionId;

    /**
     * Constructeur : création du lien avec la bdd au format SQLite
     * @param context
     */
    private LocalAccess(Context context){
        dbAccess = new MySQLiteOpenHelper(context, dbName, dbVersion);
    }

    /**
     * Création d'une instance unique de la classe
     * @param context
     * @return instance unique de la classe
     */
    public static LocalAccess getInstance(Context context){
        if(instance == null) {
            instance = new LocalAccess(context);
        }
        return instance;
    }

    public void addEntry(ModelInterface model) {
        db = dbAccess.getWritableDatabase();
        long id = db.insert(model.getTableName(), null, model.getContentValues());

        db.close();

    }

    /**
     * Adds a Point
     */
    public void addPoint(Double[] coords) {
        db = dbAccess.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("latitude", coords[0]);
        values.put("longitude", coords[1]);

        db.insert("point_coordinates", null, values);

        db.close();
    }

    public Double[] recupDernier(){
        Double[] coords = new Double[2];
        db = dbAccess.getReadableDatabase();
        String req = "select * from plant";
        Cursor curseur = db.rawQuery(req, null);
        curseur.moveToLast();
        if(!curseur.isAfterLast()){
            coords[0] = curseur.getDouble(2);
            coords[1] = curseur.getDouble(3);
        }
        curseur.close();
        db.close();
        return coords;
    }

    public RegionSelector getRegionSelector() {
        db = dbAccess.getReadableDatabase();
        return new RegionSelector(db);
    }

    public void getNextRegionPoints() {

    }

    public String select() {
        db = dbAccess.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from plant", null);
        String s = cursor.getString(1);
        cursor.close();
        db.close();
//        return "not a value just ignore this";
        return s;
    }

//    public ModelInterface[] retrieveEntries() {
//        db = dbAccess.getReadableDatabase();
//        Cursor cursor = db.rawQuery("select * from plant", null);
//        ArrayList<ModelInterface> entries = new ArrayList<>();
//        cursor.moveToLast();
//
//        while (!cursor.isAfterLast()) {
//            String label = cursor.getString(1);
//            LatLng latLng = new LatLng(cursor.getDouble(2), cursor.getDouble(3));
//            int nbLeaves = cursor.getInt(4);
//            int growthState = cursor.getInt(5);
//            entries.add(new Plant(label, latLng, nbLeaves, growthState));
//        }
//        cursor.close();
//        db.close();
//        return entries.toArray(new ModelInterface[0]);
//    }

    // Assuming you have appropriate imports for:
//   - SQLiteDatabase (or equivalent for your database system)
//   - Cursor
//   - ArrayList
//   - LatLng (if using Google Maps or equivalent)
//   - Plant model class (ensure correct attributes)

    public ModelInterface[] retrieveEntries() {
        Log.d("retrieveEntries", Arrays.toString(recupDernier()));
        db = dbAccess.getReadableDatabase();
        List<Plant> entries = new ArrayList<>();


        try {
            String query = "SELECT * FROM plant";
            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToLast()) {
                do {
                    String label = cursor.getString(1);
                    LatLng latLng = new LatLng(cursor.getDouble(2),
                            cursor.getDouble(3));
                    int nbLeaves = cursor.getInt(4);
                    int growthState = cursor.getInt(5);

                    Plant plant = new Plant(label, latLng, nbLeaves, growthState);
                    entries.add(plant);
                } while (cursor.moveToPrevious());
            } else {
                Log.e("NotFoundException", "No plants were found");
            }

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }

        return entries.toArray(new ModelInterface[0]);
    }

    public void updateEntry(ModelInterface model) {
        db = dbAccess.getReadableDatabase();
        db.update(model.getTableName(), model.getContentValues(), "where id = ?",
                new String[]{String.valueOf(model.getId())});

        db.close();
    }
}