package com.bezirk.componentManager;

import java.util.Observable;
import java.util.Observer;

public class LifeCycleObserver implements Observer {

    private LifecycleManager lifecycleManager;

    @Override
    public void update(Observable o, Object arg) {
        this.lifecycleManager = (LifecycleManager) o;
        switch (lifecycleManager.getState()) {
            case CREATED: //do something when application is created
            case STARTED: //do something when application is started
            case DESTROYED: //do something when application is destroyed
        }
    }
}
