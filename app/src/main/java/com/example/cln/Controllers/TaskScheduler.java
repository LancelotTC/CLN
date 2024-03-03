package com.example.cln.Controllers;

import android.content.Context;

import com.example.cln.Remote.Task;

import java.util.ArrayList;

@Deprecated
public class TaskScheduler {
    private static TaskScheduler instance;
    private final ArrayList<Task> scheduledTasks;

    private TaskScheduler() {
        scheduledTasks = new ArrayList<>();
    }

    public static TaskScheduler getInstance(Context context) {
        if (instance == null) {
            instance = new TaskScheduler();
        }

        return instance;
    }

    public void scheduleTask(Task task) {
//        Collections.addAll(scheduledTasks, tasks);
        scheduledTasks.add(task);
        runTasks();
    }

    private void runTasks() {
        for (Task scheduledTask : (ArrayList<Task>) scheduledTasks.clone()) {
            try {
                scheduledTask.run();
                scheduledTasks.remove(scheduledTask);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



}
