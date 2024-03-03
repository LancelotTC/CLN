package com.example.cln.Utils;

import com.example.cln.Remote.Task;

/**
 * Interface to describe a callable that accepts no arguments and returns no arguments.
 * Is Runnable not good enough? Used in {@link Task} but {@link Task} not used.
 */
public interface Lambda {
    void run();
}
