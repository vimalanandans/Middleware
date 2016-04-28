package com.bezirk.pipe.android;

import com.bezirk.pipe.core.PipeRegistry;

public class PipeRegistryFactory {
    private static PipeRegistry registry;

    protected PipeRegistryFactory() {
        //constructor for sub class
    }

    static public PipeRegistry getPipeRegistry() {
        synchronized (PipeRegistryFactory.class) {
            if (registry == null) {
                registry = new PipeRegistry();
            }
            return registry;
        }
    }
}
