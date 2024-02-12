package com.example.cln.AsyncTasks;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONException;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TaskRunner {
    private final Executor executor = Executors.newSingleThreadExecutor(); // change according to your requirements
    private final Handler handler = new Handler(Looper.getMainLooper());

    public interface Callback<R> {
        void onComplete(R result) throws JSONException;
    }

    public <R> void executeAsync(Callable<R> callable, Callback<R> callback) {
        executor.execute(() -> {
            final R result;
            try {
                result = callable.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            handler.post(() -> {
                try {
                    callback.onComplete(result);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            });
        });
    }
}
