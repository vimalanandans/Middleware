package com.bezirk.componentManager.example1;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

/**
 * This component depends on another component, {@link ComponentB} and some other startup configurations(generally maintained by the component manager & independent of other components).
 *
 */
public class ComponentAImpl implements ComponentA {

    private final String input1;
    private final String input2;
    private final ComponentB componentB;

    @Inject
    private ComponentAImpl(@Assisted("input1") String input1, @Assisted("input2") String input2, ComponentB componentB) {
        this.input1 = input1;
        this.input2 = input2;
        this.componentB = componentB;
    }

    @Override
    public void methodA() {
        System.out.println("inside methodA()");
        System.out.println("input1: " + input1 + "\ninput2: " + input2);
        System.out.println("calling componentB.methodB() (from componentA.methodA())");
        componentB.methodB();
    }
}
