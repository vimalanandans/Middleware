package com.bezirk.middleware.core.componentManager;

import com.bezirk.middleware.core.actions.StartServiceAction;
import com.bezirk.middleware.core.actions.StopServiceAction;

public interface LifeCycleCallbacks {
    void start(StartServiceAction startServiceAction);

    void stop(StopServiceAction stopServiceAction);

    //void destroy();
}
