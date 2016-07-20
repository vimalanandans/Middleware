package com.bezirk.datastorage;


public interface ProxyPersistence {
    /**
     * Persists the ProxyRegistry
     *
     * @throws Exception if persisting is not possible
     */
    void persistBezirkProxyRegistry() throws Exception;

    /**
     * Loads the ProxyRegistry
     *
     * @return ProxyRegistry
     * @throws Exception if loading is not possible
     */
    ProxyRegistry loadBezirkProxyRegistry() throws Exception;
}
