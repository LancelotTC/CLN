//package com.example.cln.Models;
//
//import com.google.android.gms.maps.model.LatLng;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//
//public abstract class AreaModelBaseClass extends Model {
//    private final ArrayList<LatLng> latLngs = new ArrayList<>();
//    private int amount;
//    public AreaModelBaseClass(String label, LatLng[] latLngs, int amount) {
//        super(label);
//        this.latLngs.addAll(Arrays.asList(latLngs));
//        this.amount = amount;
//    }
//
//    public ArrayList<LatLng> getLatLngs() {
//        return latLngs;
//    }
//
//    public int getAmount() {
//        return amount;
//    }
//
//    public void setAmount(int amount) {
//        this.amount = amount;
//    }
//
//    @Override
//    public JSONObject toJSONObject() {
//        JSONObject jsonObject = super.toJSONObject();
//        try {
//            jsonObject.put("amount", amount);
//            jsonObject.put("latLngs", latLngs);
//        } catch (JSONException e) {
//            throw new RuntimeException(e);
//        }
//
//        return jsonObject;
//    }
//}
