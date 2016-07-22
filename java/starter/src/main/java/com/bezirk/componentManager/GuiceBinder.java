package com.bezirk.componentManager;

import com.bezirk.datastorage.DatabaseConnection;
import com.bezirk.persistence.DatabaseConnectionForJava;
import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class GuiceBinder extends AbstractModule {
    @Override
    protected void configure() {
        bind(DatabaseConnection.class).to(DatabaseConnectionForJava.class);
//        install(new FactoryModuleBuilder()
//                .implement(DatabaseConnection.class, DatabaseConnectionForJava.class)
//                .build(DatabaseConnectionFactory.class));
        install(new FactoryModuleBuilder()
                .build(RegistryStorageFactory.class));
    }
}
