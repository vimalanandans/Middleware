package com.bezirk.componentManager;

import com.bezirk.actions.StartServiceAction;
import com.bezirk.actions.StopServiceAction;

public interface LifeCycleCallbacks {
    void start(StartServiceAction startServiceAction);

    void stop(StopServiceAction stopServiceAction);

    void destroy();
}
