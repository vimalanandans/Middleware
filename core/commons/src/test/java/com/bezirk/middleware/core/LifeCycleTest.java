package com.bezirk.middleware.core;

import com.bezirk.middleware.core.componentManager.LifeCycleObservable;

import org.junit.Test;

import java.util.Observable;
import java.util.Observer;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class LifeCycleTest implements Observer {

    @Override
    public void update(Observable o, Object arg) {
        LifeCycleObservable lifeCycleObservable = (LifeCycleObservable) o;
        switch (lifeCycleObservable.getState()) {
            case RUNNING:
                System.out.println("RUNNING");
                break;
            case STOPPED:
                System.out.println("STOPPED");
                break;
        }
    }

    @Test
    public void test() {
        LifeCycleObservable lifeCycleObservable = new LifeCycleObservable();
        LifeCycleTest lifeCycleTest = new LifeCycleTest();
        lifeCycleObservable.addObserver(lifeCycleTest);

        assertTrue(lifeCycleObservable.transition(LifeCycleObservable.Transition.START));
        assertFalse(lifeCycleObservable.transition(LifeCycleObservable.Transition.START));
        assertTrue(lifeCycleObservable.transition(LifeCycleObservable.Transition.STOP));
        assertFalse(lifeCycleObservable.transition(LifeCycleObservable.Transition.STOP));
    }
}
