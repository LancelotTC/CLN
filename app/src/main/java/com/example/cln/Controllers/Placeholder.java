package com.example.cln.Controllers;

@Deprecated
public class Placeholder {
    private static Placeholder instance;
    private boolean taskRunning = false;

    private Placeholder() {
        super();
    }

    public Placeholder getInstance() {
        if (instance == null) {
            instance = new Placeholder();
        }

        return instance;
    }

    public Object runUntilSuccess(Runnable func) {
        while (true) {
            try {
                func.run();
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new Object();
    }

}
