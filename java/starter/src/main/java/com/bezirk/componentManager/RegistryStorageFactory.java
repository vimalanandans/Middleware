package com.bezirk.componentManager;

import com.bezirk.datastorage.DatabaseConnection;
import com.bezirk.datastorage.RegistryStorage;
import com.google.inject.assistedinject.Assisted;

/**
 * @author Rishabh Gulati
 */
public interface RegistryStorageFactory {
    RegistryStorage createRS(@Assisted String DBVersion);
}
