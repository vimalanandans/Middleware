/**
 * The MIT License (MIT)
 * Copyright (c) 2016 Bezirk http://bezirk.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.bezirk.middleware.core.componentManager;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Observable;


/**
 * This class provides a common mechanism for updating different middleware components regarding various {@link State}.
 * This class extends {@link Observable} and makes the {@link State} of the application <b>Observable</b> by its <b>Observers</b>
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
 *      private LifeCycleObservable lifeCycleObservable;
 *
 *      @Override
 *      public void update(Observable o, Object arg) {
 *          this.lifeCycleObservable = (LifeCycleObservable) o;
 *          switch (lifeCycleObservable.getState()) {
 *              case RUNNING:
 *                  // do something when application is running, for instance, start the threads for
 *                  // sending/receiving data
 *                  break;
 *              case STOPPED:
 *                  // do something when application is started, for instance, save the state,
 *                  // stop running threads, gracefully shutdown the component
 *                  break;
 *          }
 *      }
 * }
 *
 * }
 * </pre>
 */
public class LifeCycleObservable extends Observable {

    private static final Logger logger = LoggerFactory.getLogger(LifeCycleObservable.class);

    public enum State {RUNNING, STOPPED}

    public enum Transition {START, STOP}

    private State currentState;

    public LifeCycleObservable() {
        currentState = State.STOPPED;
    }

    public boolean transition(@NotNull final Transition transition) {
        switch (currentState) {
            case STOPPED:
                switch (transition) {
                    case START:
                        changeAndNotify(State.RUNNING);
                        return true;
                    case STOP:
                        logger.error("Requested invalid transition '" + transition + "' from '" + currentState + "'");
                        return false;
                }
                break;
            case RUNNING:
                switch (transition) {
                    case START:
                        logger.error("Requested invalid transition '" + transition + "' from '" + currentState + "'");
                        return false;
                    case STOP:
                        changeAndNotify(State.STOPPED);
                        return true;
                }
                break;
        }
        return false;
    }

    public State getState() {
        return currentState;
    }

    private void changeAndNotify(State state) {
        logger.debug("Current state being changed from '" + currentState + "' to '" + state + "'. Notifying observers");
        currentState = state;
        setChanged();
        notifyObservers();
    }
}
