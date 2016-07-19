package com.bezirk.datastorage;


public interface ProxyPersistence {
    /**
     * Persists the ProxyRegistry
     *
     * @throws Exception if persisting is not possible
     */
    public void persistBezirkProxyRegistry() throws Exception;

    /**
     * Loads the ProxyRegistry
     *
     * @return ProxyRegistry
     * @throws Exception if loading is not possible
     */
    public ProxyRegistry loadBezirkProxyRegistry() throws Exception;
}
