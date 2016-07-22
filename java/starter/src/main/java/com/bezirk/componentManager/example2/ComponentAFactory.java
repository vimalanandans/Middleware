package com.bezirk.componentManager.example2;

import com.google.inject.assistedinject.Assisted;

/**
 * @author Rishabh Gulati
 */

public interface ComponentAFactory {
    ComponentA create(@Assisted("input1") String input1, @Assisted("input2") String input2);
}
