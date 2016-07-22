package com.bezirk.componentManager.example1;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class GuiceBinder extends AbstractModule {
    @Override
    protected void configure() {
        bind(ComponentB.class).to(ComponentBImpl.class);
        install(new FactoryModuleBuilder()
                .implement(com.bezirk.componentManager.example1.ComponentA.class, com.bezirk.componentManager.example1.ComponentAImpl.class)
                .build(ComponentAFactory.class));
    }
}
