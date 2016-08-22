package com.bezirk.actions;

import java.io.Serializable;

public abstract class Action implements Serializable {
    static final long serialVersionUID = 42L;

    public abstract BezirkAction getAction();
}
