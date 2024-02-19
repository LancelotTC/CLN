package com.example.cln.Storers;

import android.content.Context;

import com.example.cln.AsyncTasks.Request;
import com.example.cln.AsyncTasks.TaskRunner;
import com.example.cln.Controllers.Controller;
import com.example.cln.Models.Composter;
import com.example.cln.Models.Filter;
import com.example.cln.Models.Model;
import com.example.cln.Models.Plant;
import com.example.cln.Models.Tree;
import com.example.cln.Shortcuts;

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
        executeRequest(Map.of("tables", "*"), "GET");
    }

    public void getOne(String id) {
        executeRequest(Map.of("id", id), "GET");
    }

    public void add(Model model) {
        executeRequest(Map.of("tables", model.getTableName(),
                "data", model.toJSONObject().toString()), "POST");
    }

     public void update(Model model) {
        executeRequest(Map.of("tables", model.getTableName(), "id", String.valueOf(model.getId()),
                "data", model.toJSONObject().toString()), "PUT");
    }

    public void delete(Model model) {
        //
        executeRequest(Map.of("tables", model.getTableName(), "id", String.valueOf(model.getId())), "DELETE");
    }

    public void onGotResponse(String output) throws JSONException {
        JSONObject response = new JSONObject(output);

        int code = response.getInt("code");
        String message = response.getString("message");

        if (code != 200) {
            Shortcuts.toast(context, "Error " + code + ": " + message);
            Shortcuts.log("HTTP Error " + code, message);
            return;
        }

        Shortcuts.log("response", response);

        if (!response.getString("method").equals("GET")) {
            return;
        }

        ArrayList<Model> models = new ArrayList<>();
        JSONObject results = new JSONObject(response.getString("result"));
        Shortcuts.log("results", results);

//        HashMap<String, Function<JSONObject, Model>> stringToClass = new HashMap<>();
//
//        stringToClass.put("plant", Plant::fromJSONObject);
//        stringToClass.put("tree", Tree::fromJSONObject);
//        stringToClass.put("filter", Filter::fromJSONObject);
//        stringToClass.put("composter", Composter::fromJSONObject);

        JSONArray plants = results.getJSONArray("plant");
        JSONArray trees = results.getJSONArray("fruit_tree");
        JSONArray filters = results.getJSONArray("filter");
        JSONArray composters = results.getJSONArray("composter");

        Shortcuts.log("plants", plants);
        Shortcuts.log("trees", trees);
        Shortcuts.log("filters", filters);
        Shortcuts.log("composters", composters);


        for (int i = 0; i < plants.length(); i++) {
            models.add(Plant.fromJSONObject(new JSONObject(plants.get(i).toString())));
        }
        for (int i = 0; i < trees.length(); i++) {
            models.add(Tree.fromJSONObject(new JSONObject(trees.get(i).toString())));
        }
        for (int i = 0; i < filters.length(); i++) {
            models.add(Filter.fromJSONObject(new JSONObject(filters.get(i).toString())));
        }
        for (int i = 0; i < composters.length(); i++) {
            models.add(Composter.fromJSONObject(new JSONObject(composters.get(i).toString())));
        }

        Controller.getInstance(context).populateMap(models.toArray(new Model[0]));
    }
}
