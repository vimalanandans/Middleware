package com.bezirk.middleware.core.actions;

public class StopServiceAction extends ServiceAction {

    private static final long serialVersionUID = 1731898551169340692L;

    @Override
    public BezirkAction getAction() {
        return BezirkAction.ACTION_STOP_BEZIRK;
    }
}
