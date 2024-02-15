package com.example.cln.Storers;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.cln.AsyncTasks.Request;
import com.example.cln.AsyncTasks.TaskRunner;
import com.example.cln.Models.Model;
import com.example.cln.Shortcuts;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class RemoteAccess {

    private static RemoteAccess instance;
    private final TaskRunner<String> taskRunner;
    private final Context context;

    private RemoteAccess(Context context) {
        this.context = context;
        taskRunner = new TaskRunner<>(this::onGotResponse);
    }

    public static RemoteAccess getInstance(Context context) {
        if (instance == null) {
            instance = new RemoteAccess(context);
        }

        return instance;
    }

    private void executeRequest(Map<String, String> map, String method) {
        taskRunner.executeAsync(new Request(map, method));
    }

    public void getAll() {
        executeRequest(Map.of(), "GET");
    }

    public void getOne(String id) {
        executeRequest(Map.of("id", id), "GET");
    }

    public void add(@NonNull JSONObject jsonObject) {
        executeRequest(Map.of("data", jsonObject.toString()), "POST");
    }

    public void update(String id, @NonNull JSONObject jsonObject) {
        executeRequest(Map.of("id", id, "data", jsonObject.toString()), "PUT");

    }

    public void delete(String id) {
        executeRequest(Map.of("id", id), "DELETE");
    }

    public void onGotResponse(String output) throws JSONException {
        JSONObject response = new JSONObject(output);

        int code = response.getInt("code");
        String message = response.getString("message");

        if (code != 200) {
            Shortcuts.toast(context, "Error " + code);
            Shortcuts.log("HTTP Error " + code, message);
            return;
        }

        String result = response.getString("result");

        ArrayList<Model> models = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(result);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject entry = new JSONObject(jsonArray.get(i).toString());
            models.add(
                    new Model(
                            entry.getString("label"),
                            new LatLng(entry.getDouble("latitude"),
                                    entry.getDouble("longitude"))
                    )
            );
        }

//        controller.populateMap(models.toArray(new Model[0]));
    }
}
