package com.bezirk.middleware.core.actions;

import java.io.Serializable;

public abstract class Action implements Serializable {

    private static final long serialVersionUID = 978832741251577122L;

    public abstract BezirkAction getAction();
}
