//package com.example.cln;
//
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.os.Build;
//
//import com.example.cln.Local.MySQLiteOpenHelper;
//import com.example.cln.Local.RegionSelector;
//import com.example.cln.Models.Composter;
//import com.example.cln.Models.Filter;
//import com.example.cln.Models.Model;
//import com.example.cln.Models.Plant;
//import com.example.cln.Models.Tree;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.Marker;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.function.Function;
//
///**
// * Classe exploitant MySQLiteOpenHelper pour manipuler la bdd au format SQLite
// */
//public class LocalAccess {
//
//    private String dbName = "cln.sqlite";
//    private Integer dbVersion = 1;
//    private final MySQLiteOpenHelper dbAccess;
//    private static LocalAccess instance;
//    private SQLiteDatabase db;
//    private final HashMap<Long, Model> objectToMarker;
//
//    private LocalAccess(Context context) {
//        dbAccess = new MySQLiteOpenHelper(context, dbName, dbVersion);
//        objectToMarker = new HashMap<>();
//    }
//
//    public static LocalAccess getInstance(Context context){
//        if(instance == null) {
//            instance = new LocalAccess(context);
//        }
//
//        return instance;
//    }
//
//    public Model getModel(Long markerTag) {
//        return objectToMarker.get(markerTag);
//    }
//
//    public Model getModel(Marker marker) {
//        return objectToMarker.get((Long) marker.getTag());
//    }
//
//    public void addModel(Long markerTag, Model model) {
//        objectToMarker.put(markerTag, model);
//    }
//
//    public void addEntry(Model model, Marker marker) {
//        db = dbAccess.getWritableDatabase();
//        long id = db.insert(model.getTableName(), null, model.getContentValues());
//        db.close();
//
//        model.setId(id);
//        marker.setTag(id);
//        addModel((Long) marker.getTag(), model);
//    }
//
//    @Deprecated
//    public RegionSelector getRegionSelector() {
//        db = dbAccess.getReadableDatabase();
//        return new RegionSelector(db);
//    }
//
//    public void getNextRegionPoints() {
//
//    }
//
//    public Plant buildPlant(Cursor cursor) {
//        String label = cursor.getString(1);
//        LatLng latLng = new LatLng(cursor.getDouble(2),
//                cursor.getDouble(3));
//        int nbLeaves = cursor.getInt(4);
//        int growthState = cursor.getInt(5);
//
//        Plant plant = new Plant(label, latLng, nbLeaves, growthState);
//        plant.setId(cursor.getLong(0));
//        return plant;
//    }
//
//    public Tree buildTree(Cursor cursor) {
//        String label = cursor.getString(1);
//        LatLng latLng = new LatLng(cursor.getDouble(2),
//                cursor.getDouble(3));
//
//        Tree tree = new Tree(label, latLng);
//        tree.setId(cursor.getLong(0));
//        return tree;
//    }
//    public Filter buildFilter(Cursor cursor) {
//        String label = cursor.getString(1);
//        LatLng latLng = new LatLng(cursor.getDouble(2),
//                cursor.getDouble(3));
//
//        Filter filter = new Filter(label, latLng);
//        filter.setId(cursor.getLong(0));
//        return filter;
//    }
//    public Composter buildComposter(Cursor cursor) {
//        String label = cursor.getString(1);
//        LatLng latLng = new LatLng(cursor.getDouble(2),
//                cursor.getDouble(3));
//
//        Composter composter = new Composter(label, latLng);
//        composter.setId(cursor.getLong(0));
//        return composter;
//    }
//
//
//    private Function<Cursor, Model> getModelBuilder(String table) {
//        HashMap<String, Function<Cursor, Model>> tableToFunction = new HashMap<>();
//
//        tableToFunction.put("plant", this::buildPlant);
//        tableToFunction.put("fruit_tree", this::buildTree);
//        tableToFunction.put("filter", this::buildFilter);
//        tableToFunction.put("composter", this::buildComposter);
//
//        return tableToFunction.get(table);
//    }
//
//    public List<Model> getAll(String table) {
//        List<Model> entries = new ArrayList<>();
//
//        Function<Cursor, Model> modelBuilder = getModelBuilder(table);
//
//        String query = "SELECT * FROM " + table;
//        Cursor cursor = db.rawQuery(query, null);
//
//
//        if (cursor.moveToLast()) {
//            do {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    entries.add(modelBuilder.apply(cursor));
//                }
//            } while (cursor.moveToPrevious());
//        }
//
//        cursor.close();
//
//        return entries;
//    }
//
//    public Model[] retrieveEntries() {
//        db = dbAccess.getReadableDatabase();
//        List<Model> entries = new ArrayList<>();
//
//        try {
//            for (String table : new String[]{"plant", "fruit_tree", "filter", "composter"}) {
//                entries.addAll(getAll(table));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (db != null && db.isOpen()) {
//                db.close();
//            }
//        }
//
//        return entries.toArray(new Model[0]);
//    }
//
//    private String getModelIdName(Model model) {
//        return model.getTableName() + "_id";
//    }
//
//    public void updateEntry(Model model) {
//        db = dbAccess.getReadableDatabase();
//
//        try {
//            db.update(model.getTableName(), model.getContentValues(), model.getTableName() + "_id = ?",
//                    new String[]{String.valueOf(model.getId())});
//        } finally {
//            db.close();
//        }
//    }
//
//    public void deleteEntry(Marker selectedMarker) {
//        Model model = objectToMarker.get((Long) selectedMarker.getTag());
//        assert model != null;
//        db = dbAccess.getWritableDatabase();
//        try {
//            db.delete(model.getTableName(), model.getTableName() + "_id = ?",
//                    new String[] {String.valueOf(model.getId())});
//        } finally {
//            db.close();
//        }
//    }
//}