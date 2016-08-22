package com.bezirk.actions;

public class StopServiceAction extends ServiceAction {
    @Override
    public BezirkAction getAction() {
        return BezirkAction.ACTION_STOP_BEZIRK;
    }
}
