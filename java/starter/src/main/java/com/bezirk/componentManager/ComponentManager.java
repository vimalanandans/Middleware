package com.bezirk.componentManager;

import com.bezirk.datastorage.DatabaseConnection;
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
    private DatabaseConnection databaseConnection;
    @Inject private RegistryStorageFactory registryStorageFactory;
    private RegistryStorage registryStorage;
    public void start() {
        //Injector injector = Guice.createInjector(new GuiceBinder());

        registryStorage = registryStorageFactory.create("1");

        try {
            //databaseConnection.getDatabaseConnection();
            //databaseConnection.getPersistenceDAO();
            registryStorage.loadSphereRegistry();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ComponentManager componentManager = new ComponentManager();
        componentManager.start();
    }
}
