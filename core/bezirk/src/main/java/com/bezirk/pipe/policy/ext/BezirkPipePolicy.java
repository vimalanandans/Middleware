package com.bezirk.pipe.policy.ext;

import com.bezirk.middleware.addressing.PipePolicy;

import java.util.HashSet;

public class BezirkPipePolicy extends PipePolicy {
    private HashSet<String> allowedProtocols = new HashSet<String>();

    public BezirkPipePolicy(PipePolicy policy) {
        this.setReasonMap(policy.getReasonMap());
        allowedProtocols.addAll(policy.getReasonMap().keySet());
    }

    @Override
    public boolean isAuthorized(String protocolRoleName) {
        return this.allowedProtocols.contains(protocolRoleName);
    }

    public HashSet<String> getAllowedProtocols() {
        return allowedProtocols;
    }

    public boolean authorize(String role) {
        if (this.getReasonMap().containsKey(role)) {
            this.allowedProtocols.add(role);
            return true;
        }
        return false;
    }

    public boolean unAuthorize(String role) {
        if (this.getReasonMap().containsKey(role)) {
            this.allowedProtocols.remove(role);
            return true;
        }
        return false;
    }
}
