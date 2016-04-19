package com.bezirk.proxy;

import com.bezirk.middleware.IBezirk;
import com.bezirk.starter.UhuConfig;


public abstract class Factory {
	private static IBezirk instance = null;

	/**
	 *   @return an object that implements UhuAPI
	 */
	public static IBezirk getInstance() {
		synchronized (Factory.class) {
            if (instance == null) {
                instance = (IBezirk) new Proxy();
            }
            return instance;
        }
	}
	
	/** TODO move this to java common */
	public static IBezirk getInstance(UhuConfig uhuConfig) {
		synchronized (Factory.class) {
            if (instance == null) {
                instance = (IBezirk) new Proxy(uhuConfig);
            }
            return instance;
        }
	}
	
}
