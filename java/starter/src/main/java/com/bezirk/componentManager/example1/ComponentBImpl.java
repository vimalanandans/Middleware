package com.bezirk.componentManager.example1;

import com.google.inject.Inject;

public class ComponentBImpl implements ComponentB {

    @Inject
    private ComponentBImpl() {
    }

    @Override
    public void methodB() {
        System.out.println("inside method B");
    }
}
