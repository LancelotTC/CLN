package com.example.cln.Storers;

import android.content.Context;

import com.example.cln.AsyncTasks.Request;
import com.example.cln.AsyncTasks.TaskRunner;
import com.example.cln.Controllers.Controller;
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
    private final TaskRunner taskRunner = new TaskRunner();
    private final Controller controller;
    private final Context context;

    private RemoteAccess(Context context) {
        this.context = context;
        controller = Controller.getInstance(context);
    }

    public static RemoteAccess getInstance(Context context) {
        if (instance == null) {
            instance = new RemoteAccess(context);
        }

        return instance;
    }

    public void get() {
        taskRunner.executeAsync(
                new Request(
                        Map.of("param1", "value1", "param2", "value2")
                ),
                this::onGotResponse
        );
    }

    private void onGotResponse(String output) throws JSONException {
        JSONObject response = new JSONObject(output);

        int code = response.getInt("code");
        String message = response.getString("message");


        if (code != 200) {
            Shortcuts.toast(context, "Error " + code);
            Shortcuts.log("HTTP Error " + code, message);
            return;
        }
        String result = response.getString("result");

        ArrayList<Model> models = new ArrayList<Model>();
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

        controller.populateMap(models.toArray(new Model[0]));
    }
}
