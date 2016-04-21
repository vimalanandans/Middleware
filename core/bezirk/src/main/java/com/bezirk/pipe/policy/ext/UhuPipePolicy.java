package com.bezirk.pipe.policy.ext;

import com.bezirk.middleware.addressing.PipePolicy;

import java.util.HashSet;

public class UhuPipePolicy extends PipePolicy {
    private HashSet<String> allowedProtocols = new HashSet<String>();

    public UhuPipePolicy(PipePolicy policy) {
        this.setReasonMap(policy.getReasonMap());
        allowedProtocols.addAll(policy.getReasonMap().keySet());
    }

    @Override
    public boolean isAuthorized(String pRoleName) {
        return this.allowedProtocols.contains(pRoleName);
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
