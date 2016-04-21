package com.bezirk.persistence;


public interface IUhuProxyPersistence {
    /**
     * Persists the UhuProxyRegistry
     *
     * @throws Exception if persisting is not possible
     */
    public void persistUhuProxyRegistry() throws Exception;

    /**
     * Loads the UhuProxyRegistry
     *
     * @return UhuProxyRegistry
     * @throws Exception if loading is not possible
     */
    public UhuProxyRegistry loadUhuProxyRegistry() throws Exception;
}
