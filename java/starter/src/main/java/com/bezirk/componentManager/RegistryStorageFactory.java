package com.bezirk.componentManager;

import com.bezirk.datastorage.RegistryStorage;

/**
 * @author Rishabh Gulati
 */
public interface RegistryStorageFactory {
    RegistryStorage create(String DBVersion);
}
