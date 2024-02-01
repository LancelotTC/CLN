package com.example.cln.AsyncTasks;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class test implements AsyncResponse {
    public void envoi() {
        httpcls https = new httpcls();
        https.delegate = this;

        https.execute("https://apicarto.ign.fr/api/cadastre/commune");
    }

    @Override
    public void processFinish(String output) throws JSONException {
        JSONObject response = new JSONObject(output);

        JSONArray features = new JSONArray(response.getString("features"));
        JSONArray geometry = new JSONArray(response.getString("geometry"));
        JSONArray coordinates = new JSONArray(response.getString("coordinates"));
//        firstItem = coordinates[0];

    }
}
