package com.bezirk.componentManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger logger = LoggerFactory.getLogger(LifecycleManager.class);

    public enum LifecycleState {CREATED, STARTED, STOPPED, DESTROYED}

    private LifecycleState currentState; //stores the current state of bezirk service

    /**
     * Set state of the bezirk service if the transition to the state is valid
     *
     * @param state
     * @see #validateStateTransition(LifecycleState, LifecycleState...)
     */
    void setState(LifecycleState state) {
        if (currentState == null) {
            validateStateTransition(state, LifecycleState.CREATED);
        } else {
            switch (currentState) {
                case CREATED:
                    validateStateTransition(state, LifecycleState.STARTED);
                    break;
                case STARTED:
                    validateStateTransition(state, LifecycleState.STOPPED);
                    break;
                case STOPPED:
                    validateStateTransition(state, LifecycleState.STARTED, LifecycleState.DESTROYED);
                    break;
                case DESTROYED:
                    break;
            }
        }
    }

    /**
     * Validate transition from one {@link LifecycleState} state to another.
     *
     * @param requestedState           state to which the transition needs to be made
     * @param possibleStateTransitions possible states to transition to based on the {@link #currentState}
     */
    private void validateStateTransition(LifecycleState requestedState, LifecycleState... possibleStateTransitions) {
        for (LifecycleState possibleStateTransition : possibleStateTransitions) {
            if (requestedState == possibleStateTransition) {
                changeAndNotify(requestedState);
                return;
            }
        }
        logger.debug("Current State '" + currentState + "' Requested State '" + requestedState + "'received");
    }

    private void changeAndNotify(LifecycleState state) {
        this.currentState = state;
        setChanged();
        notifyObservers();
        logger.debug("Current state changed to '" + state + "'. Notifying observers");
    }

    public LifecycleState getState() {
        return currentState;
    }

}
