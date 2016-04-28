package com.bezirk.pipe.core;

import com.bezirk.pipe.policy.ext.BezirkPipePolicy;

import java.util.HashMap;

public final class PipePolicyUtility {
    public static final HashMap<String, BezirkPipePolicy> policyInMap = new HashMap<String, BezirkPipePolicy>();
    public static final HashMap<String, BezirkPipePolicy> policyOutMap = new HashMap<String, BezirkPipePolicy>();
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

