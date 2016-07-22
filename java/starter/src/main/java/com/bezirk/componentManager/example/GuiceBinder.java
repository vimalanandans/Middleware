package com.bezirk.componentManager.example;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;

public class GuiceBinder extends AbstractModule {
    @Override
    protected void configure() {
        bind(ComponentB.class).to(ComponentBImpl.class);
        install(new FactoryModuleBuilder()
                .implement(ComponentA.class, ComponentAImpl.class)
                .build(ComponentAFactory.class));
    }
}
