package com.bezirk.componentManager.example2;

/**
 * @author Rishabh Gulati
 */
public class ComponentAImplParent {

    private final ComponentB componentB;

    protected ComponentAImplParent(ComponentB componentB) {
        this.componentB = componentB;
    }

    protected void methodC(){
        System.out.println("inside methodC()");
    }
}
