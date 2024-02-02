package com.example.cln.Storers;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {

    // propriété de création d'une table dans la base de données
    private final String growthStateTableCreationQuery =
            "CREATE TABLE growth_state(growth_state_id INTEGER PRIMARY KEY AUTOINCREMENT, label VARCHAR(50) NOT NULL);";
    private final String fruitTreeTableCreationQuery =
            "CREATE TABLE fruit_tree(fruit_tree_id INTEGER PRIMARY KEY AUTOINCREMENT,label VARCHAR(50) NOT NULL,latitude DECIMAL(15,13) NOT NULL,longitude DECIMAL(16,13) NOT NULL);";
    private final String plantTableCreationQuery =
            "CREATE TABLE plant(plant_id INTEGER PRIMARY KEY AUTOINCREMENT, label VARCHAR(50) NOT NULL, latitude DECIMAL(15,13) NOT NULL, longitude DECIMAL(16,13) NOT NULL, nb_leaves INT, growth_state_id INT NOT NULL,FOREIGN KEY (growth_state_id) REFERENCES growth_state(growth_state_id));";
    private final String filterTableCreationQuery =
            "CREATE TABLE filter(filter_id INTEGER PRIMARY KEY AUTOINCREMENT, label VARCHAR(50) NOT NULL, latitude DECIMAL(15, 13) NOT NULL, longitude DECIMAL(16,13) NOT NULL);";
    private final String composterTableCreationQuery =
            "CREATE TABLE composter(composer_id INTEGER PRIMARY KEY AUTOINCREMENT, label VARCHAR(50) NOT NULL, latitude DECIMAL(15, 13) NOT NULL, longitude DECIMAL(16,13) NOT NULL);";

//            "CREATE TABLE growth_state(" +
//                "  growth_state_id INTEGER PRIMARY KEY AUTOINCREMENT," +
//                "label VARCHAR(50) NOT NULL" +
//                ");" +
//            "CREATE TABLE fruit_tree(" +
//                "   fruit_tree_id INTEGER PRIMARY KEY AUTOINCREMENT," +
//                "   label VARCHAR(50) NOT NULL," +
//                "   latitude DECIMAL(15,13) NOT NULL," +
//                "   longitude DECIMAL(16,13) NOT NULL" +
//                ");" +
//            "CREATE TABLE plant(" +
//                "   plant_id INTEGER PRIMARY KEY AUTOINCREMENT," +
//                "   label VARCHAR(50) NOT NULL," +
//                "   latitude DECIMAL(15,13) NOT NULL," +
//                "   longitude DECIMAL(16,13) NOT NULL," +
//                "   growth_state_id INT NOT NULL," +
//                "   FOREIGN KEY (growth_state_id) REFERENCES growth_state(growth_state_id)" +
//                ");"
//            ;
//            "CREATE TABLE Commune(" +
//                    "   commune_id INTEGER AUTOINCREMENT," +
//                    "   label VARCHAR(50) NOT NULL," +
//                    "   PRIMARY KEY(commune_id)," +
//                    "   UNIQUE(label)" +
//                    ");" +
//                    "" +
//                    "CREATE TABLE Point(" +
//                    "point_id INTEGER PRIMARY KEY" +
//                    "   commune_id INTEGER," +
//                    "   latitude DECIMAL(15,13)," +
//                    "   longitude DECIMAL(16,13)," +
//                    "   PRIMARY KEY(point_id AUTO_INCREMENT, commune_id, latitude, longitude)," +
//                    "   FOREIGN KEY(commune_id) REFERENCES Commune(commune_id)" +
//                    ");";

    /**
     * Construction de l'accès à une base de données locale
     * @param context
     * @param name
     * @param version
     */
    public MySQLiteOpenHelper(Context context, String name, int version) {
        super(context, name, null, version);
        // TODO Auto-generated constructor stub
    }

    /**
     * méthode redéfinie appelée automatiquement par le constructeur
     * uniquement si celui-ci repère que la base n'existe pas encore
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
//        Log.d("database", creation);
        db.execSQL(growthStateTableCreationQuery);
        db.execSQL(plantTableCreationQuery);
        db.execSQL(fruitTreeTableCreationQuery);
        db.execSQL(filterTableCreationQuery);
        db.execSQL(composterTableCreationQuery);

        // Populating growth_state
        ContentValues cv = new ContentValues();
        ArrayList<Long> values = new ArrayList<>();

        cv.put("label", "Petit soldat");
        values.add(db.insert("growth_state", null, cv));
        cv.clear();

        cv.put("label", "Papillon");
        values.add(db.insert("growth_state", null, cv));
        cv.clear();

        cv.put("label", "Feuille");
        values.add(db.insert("growth_state", null, cv));
        Log.d("Population result", values.toString());
    }

    /**
     * méthode redéfinie appelée automatiquement s'il y a changement de version de la base
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }

}
