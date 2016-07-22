package com.bezirk.componentManager;

import com.bezirk.datastorage.RegistryStorage;
import com.google.inject.Guice;
import com.google.inject.Injector;

import javax.inject.Inject;

/**
 * This class is used for
 * <ul>
 * <li>Managing Bezirk lifecycle, eg. create, start, pause, stop, destroy, etc</li>
 * <li>Injecting dependencies among various components</li>
 * <li>Launching Bezirk with some pre-defined configurations regarding which components to be initialized and injected</li>
 * </ul>
 */


public class ComponentManager {

    @Inject
    private RegistryStorageFactory registryStorageFactory;

    @Inject
    private ComponentManager() {
    }

    public void start() {
        RegistryStorage registryStorage = registryStorageFactory.createRS("0.0.5");
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