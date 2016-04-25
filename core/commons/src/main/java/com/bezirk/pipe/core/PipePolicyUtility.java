package com.bezirk.pipe.core;

import com.bezirk.pipe.policy.ext.UhuPipePolicy;

import java.util.HashMap;

public final class PipePolicyUtility {
    public static final HashMap<String, UhuPipePolicy> policyInMap = new HashMap<String, UhuPipePolicy>();
    public static final HashMap<String, UhuPipePolicy> policyOutMap = new HashMap<String, UhuPipePolicy>();
    public static final HashMap<String, PipeRequester> pipeRequesterMap = new HashMap<String, PipeRequester>();

    private PipePolicyUtility() {
        //this is a utility class which stores selected policies
    }

    public static void removeId(String id) {
        policyInMap.remove(id);
        policyOutMap.remove(id);
        pipeRequesterMap.remove(id);
    }

}

