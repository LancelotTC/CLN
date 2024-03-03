package com.example.cln.Utils;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONException;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Run task concurrently
 * @param <R> response type, in other words the type of the parameter of the callback function
 */
public class TaskRunner <R> {
    private final Executor executor = Executors.newSingleThreadExecutor(); // change according to your requirements
    private final Handler handler = new Handler(Looper.getMainLooper());

    private final Callback<R> callback;

    public interface Callback<R> {
        void onComplete(R result) throws JSONException;
    }

    /**
     * Constructor
     * @param callback type of the parameter of the callback function
     */
    public TaskRunner(Callback<R> callback) {
        super();
        this.callback = callback;
        }

    /**
     * Calls the callable asynchronously
     * @param callable callable object
     */
    public void executeAsync(Callable<R> callable) {
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
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        });
    }
}
