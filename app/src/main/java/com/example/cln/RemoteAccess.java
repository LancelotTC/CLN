package com.example.cln;

import android.content.Context;

import com.example.cln.Models.Composter;
import com.example.cln.Models.Filter;
import com.example.cln.Models.Model;
import com.example.cln.Models.Plant;
import com.example.cln.Models.Tree;
import com.example.cln.Remote.Request;
import com.example.cln.Utils.Shortcuts;
import com.example.cln.Utils.TaskRunner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

/**
 * "Controller" responsible of sending requests to the database and receiving outputs.
 */
public class RemoteAccess {

    /**
     * Static instance of class for Singleton design pattern
     */
    private static RemoteAccess instance;


    /**
     * {@link TaskRunner} instance used to asynchronously handle any tasks
     */
    private final TaskRunner<String> taskRunner;

    /**
     * Contains the context to avoid having to include it in every single method call.
     * Check {@link MapController#context} for risks of storing a reference here.
     */
    private final Context context;

    /**
     * Class constructor
     * @param context Activity in which the instance should be used.
     */
    private RemoteAccess(Context context) {
        this.context = context;
        taskRunner = new TaskRunner<>(this::onGotResponse);
    }

    /**
     * Public method to get the sole instance for Singleton design pattern
     * @param context Activity in which the instance should be used.
     * @return RemoteAccess
     */
    public static RemoteAccess getInstance(Context context) {
        if (instance == null) {
            instance = new RemoteAccess(context);
        }

        return instance;
    }

    /**
     * Convenience method that allows us to avoid boiler plate code.
     * @param map map of the parameters with their values
     * @param method Request method: GET, POST, PUT, DELETE supported by the server for now.
     */
    private void executeRequest(Map<String, String> map, String method) {
        taskRunner.executeAsync(new Request(map, method));
    }

    /**
     * Requests all the entries of all the relevant tables in the database
     */
    public void getAll() {
        Map<String, String> map = new java.util.HashMap<>();
        map.put("tables", "*");
        executeRequest(map, "GET");
    }

    @Deprecated
    public void getOne(String id) {
        Map<String, String> map = new java.util.HashMap<>();
        map.put("id", id);
        executeRequest(map, "GET");
    }

    /**
     * Requests the insertion of a singular Model. Corresponding table with be inferred
     * from the getTableName method present in all child classes of Model.
     * @param model Model to be added
     */
    public void add(Model model) {
        Shortcuts.log("JSONObject", model.toJSONObject().toString());
        Map<String, String> map = new java.util.HashMap<>();
        map.put("tables", model.getTableName());
        map.put("data", model.toJSONObject().toString());
        executeRequest(map, "POST");
    }

    /**
     * Requests the update of a model. Corresponding id and table will be inferred from
     * the {@link Model#getId()} method and getTableName method present in all child
     * classes of Model
     * @param model Model to be updated
     */
     public void update(Model model) {
         Map<String, String> map = new java.util.HashMap<>();
         map.put("tables", model.getTableName());
         map.put("id", String.valueOf(model.getId()));
         map.put("data", model.toJSONObject().toString());
         executeRequest(map, "PUT");
    }

    /**
     * Requests the deletion of a model. Corresponding id and table will be inferred from
     * the {@link Model#getId()} method and getTableName method present in all child
     * classes of Model
     * @param model Model to be deleted
     */
    public void delete(Model model) {
        Map<String, String> map = new java.util.HashMap<>();
        map.put("tables", model.getTableName());
        map.put("id", String.valueOf(model.getId()));
        executeRequest(map, "DELETE");
    }

    /**
     * Callback method that handles the response and populates the map if GET method used.
     * @param output JSON output from server
     * @throws JSONException Sometimes the response isn't JSON valid.
     */
    public void onGotResponse(String output) throws JSONException {
        Shortcuts.log("output", output);

        JSONObject response = new JSONObject(output);

        int code = response.getInt("code");
        String message = response.getString("message");

        // INFO: Other status codes in the 200 range still mean that the request was successful,
        // especially the 201 which by convention indicates that something was changed (POST).
        if (code != 200) {
            Shortcuts.toast(context, "Error " + code + ": " + message);
            Shortcuts.log("HTTP Error " + code, message);
            return;
        }

        JSONObject results = new JSONObject(response.getString("result"));


        switch (response.getString("method")) {
            case "GET":
                Shortcuts.log(results.toString());
                getCallback(results);
                break;
            case "POST":
                postCallback(results);
                break;
            case "PUT":
                putCallback(results);
                break;
            case "DELETE":
                deleteCallback(results);
                break;
        }
    }


    private void getCallback(JSONObject results) throws JSONException {
        ArrayList<Model> models = new ArrayList<>();



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

//        Shortcuts.log("plants", plants);
//        Shortcuts.log("trees", trees);
//        Shortcuts.log("filters", filters);
//        Shortcuts.log("composters", composters);


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

    private void postCallback(JSONObject results) {
        Controller.getInstance(context).addEntryCallback(Plant.fromJSONObject(results));
    }

    private void putCallback(JSONObject results) {

    }

    private void deleteCallback(JSONObject results) {

    }
}
