package com.bezirk.componentManager.example;

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
