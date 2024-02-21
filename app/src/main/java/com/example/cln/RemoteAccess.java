package com.example.cln;

import android.content.Context;

import com.example.cln.Models.Composter;
import com.example.cln.Models.Filter;
import com.example.cln.Models.Model;
import com.example.cln.Models.Plant;
import com.example.cln.Models.Tree;
import com.example.cln.Utils.TaskRunner;
import com.example.cln.Utils.Shortcuts;

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
//        taskRunner.executeAsync(new Request(map, method));
    }

    /**
     * Requests all the entries of all the relevant tables in the database
     */
    public void getAll() {
        executeRequest(Map.of("tables", "*"), "GET");
    }

    @Deprecated
    public void getOne(String id) {
        executeRequest(Map.of("id", id), "GET");
    }

    /**
     * Requests the insertion of a singular Model. Corresponding table with be inferred
     * from the getTableName method present in all child classes of Model.
     * @param model Model to be added
     */
    public void add(Model model) {
        executeRequest(Map.of("tables", model.getTableName(),
                "data", model.toJSONObject().toString()), "POST");
    }

    /**
     * Requests the update of a model. Corresponding id and table will be inferred from
     * the {@link Model#getId()} method and getTableName method present in all child
     * classes of Model
     * @param model Model to be updated
     */
     public void update(Model model) {
        executeRequest(Map.of("tables", model.getTableName(), "id", String.valueOf(model.getId()),
                "data", model.toJSONObject().toString()), "PUT");
    }

    /**
     * Requests the deletion of a model. Corresponding id and table will be inferred from
     * the {@link Model#getId()} method and getTableName method present in all child
     * classes of Model
     * @param model Model to be deleted
     */
    public void delete(Model model) {
        executeRequest(Map.of("tables", model.getTableName(), "id", String.valueOf(model.getId())), "DELETE");
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
