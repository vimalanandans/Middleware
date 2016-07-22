package com.bezirk.componentManager.example;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class ComponentManager {

    @Inject
    private ComponentAFactory componentAFactory;

    @Inject
    private ComponentManager() {
    }

    public void start() {
        ComponentA componentA = this.componentAFactory.create("string-input-1", "string-input-2");
        componentA.methodA();
    }

    public static void main(String[] args) {
        //create the injector using the bindings(abstract types -> concrete types) for the application
        Injector injector = Guice.createInjector(new GuiceBinder());

        //get the instance of the main application from the injector
        ComponentManager componentManager = injector.getInstance(ComponentManager.class);

        //start the application
        componentManager.start();
    }

}
