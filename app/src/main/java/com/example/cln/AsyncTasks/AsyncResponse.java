package com.example.cln.AsyncTasks;

import org.json.JSONException;

/**
 * Created by emds on 07/08/2015.
 */
public interface AsyncResponse {
    void processFinish(String output) throws JSONException;
}
