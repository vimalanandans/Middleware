package com.bezirk.componentManager;

import com.bezirk.datastorage.DatabaseConnection;
import com.bezirk.datastorage.RegistryStorage;
import com.bezirk.persistence.DatabaseConnectionForJava;
import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

/**
 * @author Rishabh Gulati
 */
public class GuiceBinder extends AbstractModule {
    @Override
    protected void configure() {
        //example
        bind(DatabaseConnection.class).to(DatabaseConnectionForJava.class);
        install(new FactoryModuleBuilder()
                .implement(RegistryStorage.class, RegistryStorage.class)
                .build(RegistryStorageFactory.class));
        //bind(RegistryStorage.class).to(DatabaseConnectionForJava.class);
    }
}
