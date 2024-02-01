package com.example.cln.Storers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Classe exploitant MySQLiteOpenHelper pour manipuler la bdd au format SQLite
 */
public class LocalAccess {

    private String dbName = "bdCoach.sqlite";
    private Integer dbVersion = 1;
    private MySQLiteOpenHelper dbAccess;
    private static LocalAccess instance;
    private SQLiteDatabase db;
    private Integer regionId;

    /**
     * Constructeur : création du lien avec la bdd au format SQLite
     * @param context
     */
    private LocalAccess(Context context){
        dbAccess = new MySQLiteOpenHelper(context, dbName, dbVersion);
        dbAccess.getWritableDatabase();
        dbAccess.close();
    }

    /**
     * Création d'une instance unique de la classe
     * @param context
     * @return instance unique de la classe
     */
    public static LocalAccess getInstance(Context context){
        if(instance == null){
            instance = new LocalAccess(context);
        }
        return instance;
    }

    /**
     * Adds a Point
     */
    public void addPoint(Double[] coords){
        db = dbAccess.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("latitude", coords[0]);
        values.put("longitude", coords[1]);

        db.insert("point_coordinates", null, values);

        db.close();
    }

    /**
     * Récupération du dernier profil enregistré dans la bdd
     * @return dernier profil
     */
    public Double[] recupDernier(){
        Double[] coords = null;
        db = dbAccess.getReadableDatabase();
        String req = "select * from profil";
        Cursor curseur = db.rawQuery(req, null);
        curseur.moveToLast();
        if(!curseur.isAfterLast()){
            coords[0] = curseur.getDouble(0);
            coords[0] = curseur.getDouble(1);
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
}