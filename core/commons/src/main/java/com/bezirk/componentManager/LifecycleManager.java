package com.bezirk.componentManager;

import java.util.Observable;

/**
 * This class provides a common mechanism for updating different middleware components regarding various {@link LifecycleState}.
 * This class extends {@link Observable} and makes the {@link LifecycleState} of the application <b>Observable</b> by its <b>Observers</b>
 * <p>
 * An observer for tracking lifecycle changes can be implemented as follows:
 * </p>
 * <pre>
 * {@code
 *
 * import java.util.Observable;
 * import java.util.Observer;
 *
 * public class LifeCycleObserver implements Observer {
 *      private LifecycleManager lifecycleManager;
 *      @Override
 *      public void update(Observable o, Object arg) {
 *          this.lifecycleManager = (LifecycleManager) o;
 *          switch (lifecycleManager.getState()) {
 *               case CREATED: //do something when application is created
 *               case STARTED: //do something when application is started
 *               case DESTROYED: //do something when application is destroyed
 *           }
 *      }
 * }
 *
 * }
 * </pre>
 */
public class LifecycleManager extends Observable {

    public enum LifecycleState {CREATED, STARTED, STOPPED, DESTROYED}

    private LifecycleState state;

    void setState(LifecycleState state) {
        this.state = state;
        setChanged();
        notifyObservers();
    }

    public LifecycleState getState() {
        return state;
    }

}
