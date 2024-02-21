package com.example.cln.Utils;

import android.content.Context;

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
