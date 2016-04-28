package com.bezirk.persistence;


public interface BezirkProxyPersistence {
    /**
     * Persists the BezirkProxyRegistry
     *
     * @throws Exception if persisting is not possible
     */
    public void persistUhuProxyRegistry() throws Exception;

    /**
     * Loads the BezirkProxyRegistry
     *
     * @return BezirkProxyRegistry
     * @throws Exception if loading is not possible
     */
    public BezirkProxyRegistry loadUhuProxyRegistry() throws Exception;
}
