package com.example.cln.Remote;

import com.example.cln.Utils.Lambda;

import java.util.ArrayList;

public class Task {
    public Lambda callable;
    public ArrayList<?> arguments;

    public Task(Lambda callable, ArrayList<?> arguments) {
        this.callable = callable;
        this.arguments = arguments;
    }

    public void run() {
//        callable.run(arguments);
    }
}
