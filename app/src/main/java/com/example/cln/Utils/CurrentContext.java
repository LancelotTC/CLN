package com.example.cln.Utils;

import android.content.Context;

/**
 * Attempt to create a class to store the current so that any methods that need it don't need
 * a parameter for it. No usages
 */
public class CurrentContext {
    public Context currentContext;

    public CurrentContext(Context currentContext) {
        this.currentContext = currentContext;
    }

    public Context getCurrentContext() {
        return currentContext;
    }

    public void setCurrentContext(Context currentContext) {
        this.currentContext = currentContext;
    }
}
